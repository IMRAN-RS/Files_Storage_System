package dev.imran.files.Service;
import dev.imran.files.Entity.Attachment;
import dev.imran.files.Repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    @Transactional
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence: " + fileName);
            }

            logger.info("Saving file: {}", fileName);

            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getBytes());
            Attachment savedAttachment = attachmentRepository.save(attachment);

            logger.info("File saved successfully: {}", fileName);
            return savedAttachment;

        } catch (Exception e) {
            logger.error("Could not save file: " + fileName, e);
            throw new Exception("Could not save File: " + fileName, e);
        }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }
}