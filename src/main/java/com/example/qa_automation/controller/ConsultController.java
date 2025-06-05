package com.example.qa_automation.controller;

import com.example.qa_automation.entity.Consult;
import com.example.qa_automation.repository.ConsultRepository;
import com.example.qa_automation.service.EmailService;
import com.example.qa_automation.service.QAService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// lớp dành cho người trả lời các câu hỏi
@RestController
@RequestMapping("/api/consult")
public class ConsultController {
    @Autowired
    private QAService qaService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConsultRepository consultRepository;

    // lấy ra list cac cau hoi chua tra loi
    @GetMapping("/unanswered")
    public ResponseEntity<List<Consult>> getUnansweredConsults() {
        return ResponseEntity.ok(qaService.getUnansweredConsults());
    }

    // tra loi cau hoi chua tra loi
    @PostMapping("/answer")
    public ResponseEntity<String> answerConsult(@RequestBody AnswerRequest request) {
        Consult consult = consultRepository.findById(request.getConsultId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));
        qaService.answerConsult(request.getConsultId(), request.getAnswer());
        emailService.sendConsultAnswer(consult.getUserEmail(), consult.getQuestion(), request.getAnswer());
        return ResponseEntity.ok("Đã gửi câu trả lời");
    }

    // cac lop request
    static class AnswerRequest {
        private Long consultId;
        @NotBlank
        private String answer;
        @Email
        private String consultantEmail;

        public Long getConsultId() { return consultId; }
        public void setConsultId(Long consultId) { this.consultId = consultId; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getConsultantEmail() { return consultantEmail; }
        public void setConsultantEmail(String consultantEmail) { this.consultantEmail = consultantEmail; }
    }
}