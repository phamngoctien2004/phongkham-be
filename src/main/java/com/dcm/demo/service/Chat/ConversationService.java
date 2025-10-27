package com.dcm.demo.service.Chat;

import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.model.Conversation;
import com.dcm.demo.model.Message;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.ConversationRepository;
import com.dcm.demo.service.Chat.dto.ConversationDTO;
import com.dcm.demo.service.Chat.dto.MessageDTO;
import com.dcm.demo.service.Chat.dto.MessageResponse;
import com.dcm.demo.service.interfaces.PatientService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository repository;
    private final UserService userService;
    private final PatientService patientService;
    private final MessageService messageService;
    public List<ConversationDTO> getAll() {
        User user  = userService.getCurrentUser();
        User.Role role = user.getRole();

        if(role.equals(User.Role.LE_TAN)){
            Specification<Conversation> spec = FilterHelper.equal("responder", "LE_TAN");

            return repository.findAll(spec).stream()
                    .map(it -> {
                        ConversationDTO dto = toDTO(it);
                        dto.setNewMessage(!it.getLastReadAdmin().equals(it.getLastMessage()));
                        return dto;
                    })
                    .toList();
        }
        Patient patient = patientService.findByPhone(user.getPhone());


        List<Conversation> conversations = repository.findByPatientId(user.getId());
        List<ConversationDTO> conversationDTOs = new ArrayList<>(conversations.stream()
                .map(it -> {
                    ConversationDTO dto = toDTO(it);
                    dto.setNewMessage(!it.getLastReadPatient().equals(it.getLastMessage()));
                    return dto;
                })
                .toList());

        if(conversations.isEmpty()){
            ConversationDTO conversationDTO = create("LE_TAN");
            conversationDTOs.add(conversationDTO);
            log.info("Created default conversation for patient: {}", patient.getFullName());
        }
            return conversationDTOs;
    }

    public MessageResponse loadMessageFirst(Integer conversationId) {
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));
        User user  = userService.getCurrentUser();
        User.Role role = user.getRole();

//tham so khoi tao
        Integer lastReadId = 0;


        if(role.equals(User.Role.LE_TAN)){
            lastReadId = conversation.getLastReadAdmin();
        }else{
            lastReadId = conversation.getLastReadPatient();
        }
        int totalMessage = messageService.countMessageByConversation(conversation.getId());
        Message lastMessage = messageService.findLastestMessageByConversationId(conversation.getId()).orElse(null);

        Integer latestMessageId = lastMessage != null ? lastMessage.getId() : 0;
        int totalRead = messageService.countReadMessage(conversationId, lastReadId);
        int totalUnread = totalMessage - totalRead;
        List<Message> messages;

//      truong hop chua doc tin nhan nao
        if(lastReadId.equals(latestMessageId)) {
            messages = messageService.findLatestMessage(conversationId);
        }
//      truong hop doc xung quanh tin nhan 30
        else{
            messages = messageService.findMessagesAroundUnread(
                    conversationId,
                    lastReadId
            );
        }
        List<MessageDTO> messageDTOs = messageService.toDTOs(messages);
        updateLastRead(conversationId, role, latestMessageId);
        return MessageResponse.builder()
                .messages(messageDTOs)
                .totalMessage(totalMessage + 1)
                .totalUnread(totalUnread)
                .lastReadId(lastReadId)
                .hasMoreOld(!messages.isEmpty() && messages.get(0).getId() > 1)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void clearMessage(Integer conversationId) {
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));
        conversation.setLastReadAdmin(0);
        conversation.setLastReadPatient(0);
        conversation.setLastMessage(0);
        conversation.setMessages(new ArrayList<>());
        repository.save(conversation);
    }

    public MessageResponse loadMoreOldMessage(Integer conversationId, Integer messageId) {
        List<Message> messages = messageService.findMessageBeforeId(conversationId, messageId);
        List<MessageDTO> messageDTOs = messageService.toDTOs(messages);
        return MessageResponse.builder()
                .messages(messageDTOs)
                .hasMoreOld(!messages.isEmpty() && messages.get(0).getId() > 1)
                .build();
    }

    ConversationDTO create(String responder) {
        User user  = userService.getCurrentUser();

        if(responder.equals("LE_TAN")){
            Patient patient = patientService.findByPhone(user.getPhone());
            Conversation conversation = new Conversation();
            conversation.setPatient(user);
            conversation.setName(patient.getFullName() + " - " + patient.getPhone());
            conversation.setResponder("LE_TAN");
            repository.save(conversation);
            return toDTO(conversation);
        }

        Conversation conversation = new Conversation();
        conversation.setPatient(user);
        conversation.setResponder("AI-" + System.currentTimeMillis());
        conversation.setLastMessage(0);
        conversation.setLastReadAdmin(0);
        conversation.setLastReadPatient(0);
        repository.save(conversation);
        return toDTO(conversation);
    }


    ConversationDTO updateLastRead(Integer conversationId, User.Role role, Integer lastReadId) {
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));
        if(role.equals(User.Role.LE_TAN)){
            conversation.setLastReadAdmin(lastReadId);
        }else{
            conversation.setLastReadPatient(lastReadId);
        }
        repository.save(conversation);
        return toDTO(conversation);
    }

    public List<ConversationDTO> toDTOs(List<Conversation> conversations) {
        return conversations.stream().map(this::toDTO).toList();
    }
    public ConversationDTO toDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId().toString());
        dto.setPatientName(conversation.getName());
        dto.setResponder(conversation.getResponder());
        return dto;
    }

}
