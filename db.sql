-- =======================================================================
-- SCHEMA DATABASE V4.1 - HỆ THỐNG KHÁM BỆNH ONLINE/OFFLINE (MySQL 8+)
-- - Full hospital portal: giữ blog, banner, dich_vu_kham (gói dịch vụ)
-- - Rút gọn: KHÔNG quản lý kho/nhập thuốc (chỉ danh mục thuốc để kê đơn)
-- - Thanh toán chỉ thực hiện tại quầy (tạo hoá đơn khi check-in)
-- - Xét nghiệm chỉ được chỉ định từ danh mục dịch vụ (dich_vu_kham)
-- - Triggers kiểm tra slot active (BEFORE INSERT/UPDATE)
-- - Views: hỗ trợ lễ tân (nhắc, thăm hỏi) và hồ sơ bệnh án
-- =======================================================================
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- ====================== NGƯỜI DÙNG ======================
CREATE TABLE IF NOT EXISTS nguoi_dung (
    id_nguoi_dung INT PRIMARY KEY AUTO_INCREMENT,
	email VARCHAR(100) UNIQUE ,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(255) NOT NULL,
    sdt VARCHAR(20),
	dia_chi TEXT,
    CCCD TEXT,
    ngay_sinh DATE,
    gioi_tinh ENUM('NAM','NU'),
    vai_tro ENUM('BENH_NHAN','BAC_SI','LE_TAN','ADMIN') NOT NULL,
    anh_nguoi_dung VARCHAR(500),
    trang_thai BOOLEAN DEFAULT TRUE,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ====================== KHOA & PHÒNG ======================
CREATE TABLE IF NOT EXISTS khoa_bac_si (
    id_khoa INT PRIMARY KEY AUTO_INCREMENT,
    ten_khoa VARCHAR(255) NOT NULL,
    sdt VARCHAR(15) NOT NULL,
    mo_ta TEXT,
    trang_thai BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS phong_kham (
    id_phong_kham INT PRIMARY KEY AUTO_INCREMENT,
    ten_phong_kham VARCHAR(255) NOT NULL,
    so_phong VARCHAR(10) NOT NULL,
    id_khoa INT,
    FOREIGN KEY (id_khoa) REFERENCES khoa_bac_si(id_khoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ====================== BẰNG CẤP & BÁC SĨ ======================
CREATE TABLE IF NOT EXISTS loai_bang_cap (
    id_bang_cap INT PRIMARY KEY AUTO_INCREMENT,
    ten_bang_cap VARCHAR(255) NOT NULL,
    phi_kham DECIMAL(12,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS bac_si (
    id_bac_si INT PRIMARY KEY AUTO_INCREMENT,
    id_nguoi_dung INT NULL,
    id_khoa INT,
    id_bang_cap INT,
    so_nam_kinh_nghiem int,
    chuc_danh VARCHAR(255),
    trang_thai BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
    FOREIGN KEY (id_khoa) REFERENCES khoa_bac_si(id_khoa),
    FOREIGN KEY (id_bang_cap) REFERENCES loai_bang_cap(id_bang_cap)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS nghi_phep (
    id_nghi_phep INT PRIMARY KEY AUTO_INCREMENT,
    id_bac_si INT,
    batdau_nghi DATE,
    ketthuc_nghi DATE,
    ly_do_nghi varchar(255),
    trang_thai_nghi TINYINT COMMENT '0: Chờ duyệt, 1: Đã duyệt, 2: Từ chối',
    ngay_gui DATE,
    nguoi_duyet INT,
    FOREIGN KEY (id_bac_si) REFERENCES bac_si(id_bac_si) ON DELETE CASCADE,
    FOREIGN KEY (nguoi_duyet) REFERENCES nguoi_dung(id_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Lịch làm việc mặc định (Admin quản lý) - FIXED: Unique constraint để tránh overlap
CREATE TABLE IF NOT EXISTS lich_lam_viec_bac_si (
    id_lich INT PRIMARY KEY AUTO_INCREMENT,
    id_bac_si INT NOT NULL,
    thu_trong_tuan ENUM('T2','T3','T4','T5','T6','T7','CN') NOT NULL,
    khung_gio_bat_dau TIME NOT NULL,
    khung_gio_ket_thuc TIME NOT NULL,
    UNIQUE KEY uniq_bacsi_thu_khunggio (id_bac_si, thu_trong_tuan, khung_gio_bat_dau),
    FOREIGN KEY (id_bac_si) REFERENCES bac_si(id_bac_si),
    CONSTRAINT chk_gio_hop_le CHECK (khung_gio_bat_dau < khung_gio_ket_thuc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ====================== BỆNH NHÂN ======================
CREATE TABLE IF NOT EXISTS benh_nhan (
    id_benh_nhan INT PRIMARY KEY AUTO_INCREMENT,
    id_nguoi_dung INT ,
    ma_benh_nhan VARCHAR(20) UNIQUE, 
    nhom_mau VARCHAR(10),               -- Nhóm máu
    chieu_cao DECIMAL(5,2),             -- Chiều cao (cm)
    can_nang DECIMAL(5,2),              -- Cân nặng (kg)
    ngay_dang_ky DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ====================== DỊCH VỤ KHÁM (GÓI DỊCH VỤ & XÉT NGHIỆM) ======================
CREATE TABLE IF NOT EXISTS dich_vu_kham (
    id_dich_vu INT PRIMARY KEY AUTO_INCREMENT,
    ma_dich_vu VARCHAR(50) UNIQUE,
    ten_dich_vu VARCHAR(255) NOT NULL,
    loai ENUM('KHAM','XET_NGHIEM','CHUAN_DOAN_HINH_ANH','KHAC') DEFAULT 'KHAM',
    gia DECIMAL(15,2) DEFAULT 0,
    mo_ta TEXT,
    trang_thai BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ====================== ĐẶT LỊCH KHÁM ======================
CREATE TABLE IF NOT EXISTS dat_lich_kham (
    id_dat_lich INT PRIMARY KEY AUTO_INCREMENT,
    id_bac_si INT ,
    id_phong_kham INT,
    ho_ten VARCHAR(255) NOT NULL,
    sdt CHAR(15) NOT NULL,
    dia_chi VARCHAR(255) NOT NULL,
    email VARCHAR(50) NOT NULL,
    ngay_sinh DATE,
    ngay_kham DATE NOT NULL,
    gio_kham TIME NOT NULL,
    trang_thai ENUM('CHO_XAC_NHAN','DA_XAC_NHAN','HOAN_THANH','KHONG_DEN') DEFAULT 'CHO_XAC_NHAN',
    trieu_chung TEXT,
    ngay_dat_lich DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_xac_nhan INT,
--     CONSTRAINT chk_ngay_kham_within_4days CHECK (ngay_kham <= DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY)),
    FOREIGN KEY (id_bac_si) REFERENCES bac_si(id_bac_si),
	FOREIGN KEY (id_phong_kham) REFERENCES phong_kham(id_phong_kham),
    FOREIGN KEY (nguoi_xac_nhan) REFERENCES nguoi_dung(id_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_dat_lich_ngay_kham ON dat_lich_kham(ngay_kham, gio_kham);
CREATE INDEX idx_dat_lich_bac_si ON dat_lich_kham(id_bac_si, ngay_kham);
CREATE INDEX idx_dat_lich_trang_thai ON dat_lich_kham(trang_thai);

-- ====================== PHIẾU KHÁM ======================
CREATE TABLE IF NOT EXISTS phieu_kham_benh (
    id_phieu_kham INT PRIMARY KEY AUTO_INCREMENT,
    id_benh_nhan INT ,
    id_dat_lich INT,
    id_bac_si INT NOT NULL,
    ma_phieu_kham VARCHAR(20) UNIQUE,
    ngay_kham DATETIME DEFAULT CURRENT_TIMESTAMP,
    trieu_chung_chinh TEXT,
    kham_lam_sang TEXT,
    chan_doan TEXT,
    ke_hoach_dieu_tri TEXT,
    ghi_chu_bac_si TEXT,
    phi_kham DECIMAL(15,2) DEFAULT 0,
    tong_chi_phi DECIMAL(15,2) DEFAULT 0,
    trang_thai ENUM('DANG_KHAM','CHO_XET_NGHIEM','HOAN_THANH','HUY') DEFAULT 'DANG_KHAM',
    FOREIGN KEY (id_benh_nhan) REFERENCES benh_nhan(id_benh_nhan), -- Sửa tham chiếu từ ho_so_benh_an sang benh_nhan
    FOREIGN KEY (id_dat_lich) REFERENCES dat_lich_kham(id_dat_lich),
    FOREIGN KEY (id_bac_si) REFERENCES bac_si(id_bac_si)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE INDEX idx_phieu_kham_ngay ON phieu_kham_benh(ngay_kham);

-- ====================== XÉT NGHIỆM (LIÊN KẾT ĐẾN dich_vu_kham) chuẩn hệ thống HIS/LIS thực tế ======================
CREATE TABLE IF NOT EXISTS chi_dinh_xet_nghiem (
    id_chi_dinh INT PRIMARY KEY AUTO_INCREMENT,
    id_phieu_kham INT NOT NULL,
    id_dich_vu INT,
    id_bac_si_chi_dinh INT NOT NULL,
	trang_thai ENUM('CHO_THUC_HIEN','DANG_THUC_HIEN','HOAN_THANH','HUY_BO') DEFAULT 'CHO_THUC_HIEN',
    gia DECIMAL(15,2) DEFAULT 0,
    ngay_chi_dinh DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngay_hen_tra_ket_qua DATETIME,
    thoi_gian_vao_hang DATETIME DEFAULT CURRENT_TIMESTAMP, -- Cột hỗ trợ FIFO
    FOREIGN KEY (id_phieu_kham) REFERENCES phieu_kham_benh(id_phieu_kham),
    FOREIGN KEY (id_dich_vu) REFERENCES dich_vu_kham(id_dich_vu),
    FOREIGN KEY (id_bac_si_chi_dinh) REFERENCES bac_si(id_bac_si)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE INDEX idx_xet_nghiem_fifo ON chi_dinh_xet_nghiem(id_dich_vu, thoi_gian_vao_hang);
CREATE INDEX idx_xet_nghiem_trang_thai ON chi_dinh_xet_nghiem(trang_thai);

CREATE TABLE IF NOT EXISTS ket_qua_xet_nghiem (
    id_ket_qua INT PRIMARY KEY AUTO_INCREMENT,
    id_chi_dinh INT NOT NULL UNIQUE,
    id_bac_si_thuc_hien INT,
    ngay_thuc_hien DATETIME,
    ket_qua_chi_tiet TEXT,
    file_ket_qua VARCHAR(500),
    trang_thai ENUM('HOAN_THANH','CAN_KIEM_TRA_LAI') DEFAULT 'HOAN_THANH',
    FOREIGN KEY (id_chi_dinh) REFERENCES chi_dinh_xet_nghiem(id_chi_dinh),
    FOREIGN KEY (id_bac_si_thuc_hien) REFERENCES bac_si(id_bac_si)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ====================== DANH MỤC THUỐC & ĐƠN THUỐC ======================
CREATE TABLE IF NOT EXISTS thuoc (
    id_thuoc INT PRIMARY KEY AUTO_INCREMENT,
    ten_thuoc VARCHAR(255) NOT NULL,
    ham_luong VARCHAR(100),	
    dang_bao_che VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS don_thuoc (
    id_don_thuoc INT PRIMARY KEY AUTO_INCREMENT,
    id_phieu_kham INT NOT NULL,
    id_bac_si INT,
    ma_don_thuoc VARCHAR(20) UNIQUE,
    ngay_ke_don DATETIME DEFAULT CURRENT_TIMESTAMP,
    huong_dan_chung TEXT,
    FOREIGN KEY (id_phieu_kham) REFERENCES phieu_kham_benh(id_phieu_kham),
    FOREIGN KEY (id_bac_si) REFERENCES bac_si(id_bac_si)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS chi_tiet_don_thuoc (
    id_chi_tiet INT PRIMARY KEY AUTO_INCREMENT,
    id_don_thuoc INT NOT NULL,
    id_thuoc INT,
    so_luong INT,
    huong_dan_su_dung TEXT,
    FOREIGN KEY (id_don_thuoc) REFERENCES don_thuoc(id_don_thuoc) ON DELETE CASCADE,
    FOREIGN KEY (id_thuoc) REFERENCES thuoc(id_thuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ====================== THANH TOÁN (CHỈ KHI CHECK-IN) ======================
CREATE TABLE IF NOT EXISTS hoa_don_thanh_toan (
    id_hoa_don INT PRIMARY KEY AUTO_INCREMENT,
    id_benh_nhan INT NOT NULL,
    id_phieu_kham INT NOT NULL,
    ma_hoa_don VARCHAR(50) UNIQUE, 
    tong_tien DECIMAL(15,2) NOT NULL, -- tổng chi phí khám + dịch vụ xét nghiệm
    so_tien_thanh_toan DECIMAL(15,2) NOT NULL, -- số tiền bệnh nhân thực trả
    phuong_thuc ENUM('TIEN_MAT','THE_ATM','CHUYEN_KHOAN','QR_CODE'),
    ngay_thanh_toan DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_thu_tien INT,
    trang_thai ENUM('CHUA_THANH_TOAN','HOAN_THANH') DEFAULT 'CHUA_THANH_TOAN',
    ghi_chu TEXT,
    FOREIGN KEY (id_benh_nhan) REFERENCES benh_nhan(id_benh_nhan),
    FOREIGN KEY (id_phieu_kham) REFERENCES phieu_kham_benh(id_phieu_kham),
    FOREIGN KEY (nguoi_thu_tien) REFERENCES nguoi_dung(id_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ====================== WEBSITE / CMS ======================
CREATE TABLE IF NOT EXISTS blog (
    id_blog INT PRIMARY KEY AUTO_INCREMENT,
    tieu_de VARCHAR(255) NOT NULL,
    noi_dung TEXT,
    ngay_dang DATE,
    tac_gia VARCHAR(255),
    hinh_anh VARCHAR(255),
    trang_thai VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS banner (
    id_banner INT PRIMARY KEY AUTO_INCREMENT,
    tieu_de VARCHAR(255) NOT NULL,
    hinh_anh VARCHAR(255) NOT NULL,
    lien_ket VARCHAR(255),
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    trang_thai VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng quản lý phiên chat
CREATE TABLE IF NOT EXISTS chat_session (
    id_chat_session INT PRIMARY KEY AUTO_INCREMENT,
    id_admin INT NOT NULL,
    id_benh_nhan INT NOT NULL,
    ngay_bat_dau DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngay_ket_thuc DATETIME,
    FOREIGN KEY (id_admin) REFERENCES nguoi_dung(id_nguoi_dung),
    FOREIGN KEY (id_benh_nhan) REFERENCES benh_nhan(id_benh_nhan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS chat_message (
    id_chat_message INT PRIMARY KEY AUTO_INCREMENT,
    id_chat_session INT NOT NULL,
    id_nguoi_gui INT NOT NULL,
    id_nguoi_nhan INT NOT NULL,
    noi_dung TEXT NOT NULL,
    thoi_gian_gui DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_chat_session) REFERENCES chat_session(id_chat_session),
    FOREIGN KEY (id_nguoi_gui) REFERENCES nguoi_dung(id_nguoi_dung),
    FOREIGN KEY (id_nguoi_nhan) REFERENCES nguoi_dung(id_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE danh_gia (
    id_danh_gia INT PRIMARY KEY AUTO_INCREMENT,
    id_nguoi_dung int,
    diem_danh_gia INT CHECK (diem_danh_gia BETWEEN 1 AND 5),
    nhan_xet TEXT,
	FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)

);

-- ====================== VIEWs CHO LỄ TÂN & HỒ SƠ ======================
-- View nhắc nhở lịch khám ngày mai
CREATE OR REPLACE VIEW vw_nhac_nho_ngay_mai AS
SELECT
    dl.id_dat_lich,
    dl.ho_ten,
    dl.sdt,
    dl.email,
    dl.ngay_kham,
    dl.gio_kham,
    dl.trang_thai,
    pk.ten_phong_kham,
    nd_bs.ho_ten AS ten_bac_si
FROM dat_lich_kham dl
LEFT JOIN phong_kham pk    ON dl.id_phong_kham = pk.id_phong_kham
LEFT JOIN bac_si bs        ON dl.id_bac_si = bs.id_bac_si
LEFT JOIN nguoi_dung nd_bs ON bs.id_nguoi_dung = nd_bs.id_nguoi_dung
WHERE dl.trang_thai = 'DA_XAC_NHAN'
  AND dl.ngay_kham = DATE_ADD(CURDATE(), INTERVAL 1 DAY);





-- View thăm hỏi bệnh nhân khám hôm qua
CREATE OR REPLACE VIEW vw_tham_hoi_hom_qua AS
SELECT 
    pk.id_phieu_kham,
    bn.id_benh_nhan,
    bn.ma_benh_nhan,
    nd.ho_ten,
    nd.sdt AS so_dien_thoai,
    nd.CCCD,
    pk.ngay_kham
FROM phieu_kham_benh pk
JOIN benh_nhan bn ON pk.id_benh_nhan = bn.id_benh_nhan
JOIN nguoi_dung nd ON bn.id_nguoi_dung = nd.id_nguoi_dung
WHERE DATE(pk.ngay_kham) = DATE_SUB(CURDATE(), INTERVAL 1 DAY);

-- View hồ sơ khám bệnh
CREATE OR REPLACE VIEW vw_ho_so_kham_benh AS
SELECT 
    bn.id_benh_nhan,
    bn.ma_benh_nhan,
    nd.ho_ten AS ten_benh_nhan,
    nd.CCCD,
    pk.id_phieu_kham,
    pk.ngay_kham,
    pk.chan_doan,
    pk.phi_kham,
    bs.id_bac_si,
    nd_bs.ho_ten AS ten_bac_si,
    lb.ten_bang_cap
    
FROM benh_nhan bn
JOIN nguoi_dung nd ON bn.id_nguoi_dung = nd.id_nguoi_dung
LEFT JOIN phieu_kham_benh pk ON bn.id_benh_nhan = pk.id_benh_nhan
LEFT JOIN bac_si bs ON pk.id_bac_si = bs.id_bac_si
LEFT JOIN nguoi_dung nd_bs ON bs.id_nguoi_dung = nd_bs.id_nguoi_dung
LEFT JOIN loai_bang_cap lb ON bs.id_bang_cap = lb.id_bang_cap
ORDER BY bn.id_benh_nhan, pk.ngay_kham DESC;



-- ====================== TRIGGER TỰ ĐỘNG  ======================
DELIMITER $$
-- Trigger tạo mã bệnh nhân
CREATE TRIGGER trg_benh_nhan_ma
AFTER INSERT ON benh_nhan
FOR EACH ROW
BEGIN
    IF NEW.ma_benh_nhan IS NULL OR NEW.ma_benh_nhan = '' THEN
        UPDATE benh_nhan
        SET ma_benh_nhan = CONCAT('BN', LPAD(NEW.id_benh_nhan, 6, '0'))
        WHERE id_benh_nhan = NEW.id_benh_nhan;
    END IF;
END$$
DELIMITER ;

DELIMITER $$

-- Helper: cập nhật tổng chi phí
CREATE PROCEDURE sp_recalc_tong_chi_phi(IN p_id_phieu_kham INT)
BEGIN
  UPDATE phieu_kham_benh
  SET tong_chi_phi = COALESCE((
      SELECT SUM(gia) FROM chi_dinh_xet_nghiem WHERE id_phieu_kham = p_id_phieu_kham
  ),0) + COALESCE(phi_kham,0)
  WHERE id_phieu_kham = p_id_phieu_kham;
END$$

CREATE TRIGGER trg_xn_after_insert
AFTER INSERT ON chi_dinh_xet_nghiem
FOR EACH ROW
BEGIN
  CALL sp_recalc_tong_chi_phi(NEW.id_phieu_kham);
END$$

CREATE TRIGGER trg_xn_after_update
AFTER UPDATE ON chi_dinh_xet_nghiem
FOR EACH ROW
BEGIN
  CALL sp_recalc_tong_chi_phi(NEW.id_phieu_kham);
END$$

CREATE TRIGGER trg_xn_after_delete
AFTER DELETE ON chi_dinh_xet_nghiem
FOR EACH ROW
BEGIN
  CALL sp_recalc_tong_chi_phi(OLD.id_phieu_kham);
END$$

DELIMITER ;


DELIMITER $$

-- tạo mã hóa đơn --
CREATE TRIGGER trg_before_insert_hoa_don
BEFORE INSERT ON hoa_don_thanh_toan
FOR EACH ROW
BEGIN
    DECLARE so_thu_tu INT;

    -- Lấy số thứ tự lớn nhất trong ngày hiện tại
    SELECT 
        COALESCE(MAX(CAST(SUBSTRING_INDEX(ma_hoa_don, '-', -1) AS UNSIGNED)), 0) + 1
    INTO so_thu_tu
    FROM hoa_don_thanh_toan
    WHERE DATE(ngay_thanh_toan) = CURDATE();

    -- Gán giá trị cho ma_hoa_don
    SET NEW.ma_hoa_don = CONCAT(
        'HD',
        DATE_FORMAT(CURDATE(), '%Y%m%d'),
        '-',
        LPAD(so_thu_tu, 4, '0')
    );
END$$

DELIMITER ;


-- ====================== DỮ LIỆU KHỞI TẠO MẪU ======================
INSERT INTO loai_bang_cap (ten_bang_cap, phi_kham) VALUES
('Bác sĩ Chuyên khoa I', 300000),
('Thạc sĩ Y học', 300000),
('Bác sĩ Chuyên khoa II', 400000),
('Tiến sĩ Y học', 400000),
('Phó Giáo sư', 500000),
('Giáo sư', 600000)
ON DUPLICATE KEY UPDATE phi_kham = VALUES(phi_kham);

INSERT INTO dich_vu_kham (ma_dich_vu, ten_dich_vu, loai, gia,  mo_ta) VALUES
('DV-KHAM-CS','Khám chuyên sâu', 'KHAM', 300000,  'Khám chuyên sâu chuyên khoa'),
('DV-XN-MAU','Xét nghiệm máu cơ bản', 'XET_NGHIEM', 150000,  'Xét nghiệm máu cơ bản'),
('DV-XQ-PHOI','X-quang phổi', 'CHUAN_DOAN_HINH_ANH', 200000,  'Chụp X-quang phổi')
ON DUPLICATE KEY UPDATE gia = VALUES(gia);

-- ====================== HƯỚNG DẪN NHANH ======================

-- 

