package com.dcm.demo.service.Chat;

import com.dcm.demo.model.Conversation;
import com.dcm.demo.model.Message;
import com.dcm.demo.model.MessageImage;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.ConversationRepository;
import com.dcm.demo.repository.MessageRepository;
import com.dcm.demo.service.Chat.dto.MessageDTO;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserService userService;


    @Transactional(rollbackFor = Exception.class)
    public MessageDTO create(MessageDTO dto) {
        Message message = toEntity(dto);

        // luu tin nhan anh
        if (dto.getUrls() != null && !dto.getUrls().isEmpty()) {
            List<MessageImage> images = new ArrayList<>();
            for (String url : dto.getUrls()) {
                MessageImage image = new MessageImage();
                image.setUrl(url);
                image.setMessage(message);
                images.add(image);
            }
            message.setImages(images);
        }

        Message savedMessage = messageRepository.save(message);

        User user = savedMessage.getSender();
        if(user != null){
            User.Role role = savedMessage.getSender().getRole();
            Conversation conversation = conversationRepository.findById(savedMessage.getConversation().getId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            conversation.setLastMessage(savedMessage.getId());
            if(role.equals(User.Role.LE_TAN)){
                conversation.setLastReadAdmin(savedMessage.getId());
            } else {
                conversation.setLastReadPatient(savedMessage.getId());
            }
            conversationRepository.save(conversation);
        }

        return toDTO(savedMessage);
    }
    public void clearMessage(Integer conversationId) {
        messageRepository.deleteByConversationId(conversationId);
    }

    public Optional<Message> findLastestMessageByConversationId(Integer conversationId) {
        return messageRepository.findLastestMessageByConversationId(conversationId);
    }

    public int countMessageByConversation(Integer conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }

    public int countReadMessage(Integer conversationId, Integer lastReadId) {
        return messageRepository.countRead(conversationId, lastReadId);
    }

    List<Message> findLatestMessage(Integer conversationId) {
        return messageRepository.findMessageLatest(conversationId, 30).stream()
                .sorted(Comparator.comparing(Message::getId))
                .toList();
    }

    List<Message> findMessageBeforeId(Integer conversationId, Integer messageId) {
        return messageRepository.findMessageBeforeId(conversationId, messageId).stream()
                .sorted(Comparator.comparing(Message::getId))
                .toList();
    }

    List<Message> findMessagesAroundUnread(
            Integer conversationId,
            Integer lastReadId
    ) {
        return messageRepository.findMessagesAroundUnread(
                conversationId,
                lastReadId,
                30
        );
    }

    public List<MessageDTO> toDTOs(List<Message> messages) {
        return messages.stream().map(this::toDTO).toList();
    }

    public MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender() != null ? message.getSender().getId() : null);
        dto.setMessage(message.getMessage());
        dto.setSentTime(message.getSentTime());
        dto.setLastMessageConversation(message.getId());
        List<String> urls = message.getImages().stream()
                .map(MessageImage::getUrl).toList();
        dto.setUrls(urls);
        return dto;
    }

    public Message toEntity(MessageDTO dto) {

        Message message = new Message();
        User user = userService.getCurrentUser();

        Conversation conversation = new Conversation();
        conversation.setId(dto.getConversationId());
        message.setConversation(conversation);
        // Note: Conversation and Sender should be set separately

        if(dto.getSenderId() != null){
            message.setSender(user);
        }else{
            message.setSender(null);
        }

        message.setMessage(dto.getMessage());
        message.setSentTime(dto.getSentTime());
        return message;
    }
}
