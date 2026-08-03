package com.smartjobportal.service;

import com.smartjobportal.model.ApplicationStatus;
import com.smartjobportal.model.Interview;
import com.smartjobportal.model.JobApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean sendInterviewInvite(JobApplication application, Interview interview) {
        if (application == null || application.getCandidate() == null || application.getCandidate().getEmail() == null) {
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            String to = application.getCandidate().getEmail();
            String subject = "Interview Scheduled: " + application.getJob().getTitle();
            String meetingLink = interview.getMeetingLink();
            String location = interview.getLocation() == null ? "TBD" : interview.getLocation();
            String body = "Hello " + application.getCandidate().getName() + ",\n\n"
                    + "Your interview for the position '" + application.getJob().getTitle() + "' at " + application.getJob().getCompany() + " has been scheduled.\n\n"
                    + "Date & Time: " + interview.getScheduledAt() + "\n"
                    + "Location: " + location + "\n"
                    + (meetingLink != null ? "Join link: " + meetingLink + "\n" : "")
                    + "Notes: " + (interview.getNotes() == null ? "None" : interview.getNotes()) + "\n\n"
                    + "A calendar invitation is attached to this email.\n\n"
                    + "Best regards,\nSmart Job Portal";

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.addAttachment("interview-invite.ics", new ByteArrayResource(generateCalendarInvite(application, interview).getBytes(StandardCharsets.UTF_8)), "text/calendar; charset=UTF-8");
            mailSender.send(message);
            return true;
        } catch (MessagingException ex) {
            return false;
        }
    }

    private String generateCalendarInvite(JobApplication application, Interview interview) {
        String uid = "interview-" + application.getId() + "@smartjobportal.local";
        String dtStamp = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm'00'Z")
                .format(interview.getScheduledAt().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")));
        String dtStart = dtStamp;
        String dtEnd = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm'00'Z")
                .format(interview.getScheduledAt().plusHours(1).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")));
        String meetingLink = interview.getMeetingLink();
        String location = interview.getLocation() == null ? (meetingLink == null ? "TBD" : meetingLink) : interview.getLocation();
        String description = "Interview for " + application.getJob().getTitle() + " at " + application.getJob().getCompany() + "\n"
                + "Notes: " + (interview.getNotes() == null ? "None" : interview.getNotes()) + "\n"
                + (meetingLink != null ? "Join link: " + meetingLink + "\n" : "");

        return "BEGIN:VCALENDAR\r\n"
                + "METHOD:REQUEST\r\n"
                + "PRODID:-//Smart Job Portal//Interview Scheduler//EN\r\n"
                + "VERSION:2.0\r\n"
                + "BEGIN:VEVENT\r\n"
                + "UID:" + uid + "\r\n"
                + "DTSTAMP:" + dtStamp + "\r\n"
                + "DTSTART:" + dtStart + "\r\n"
                + "DTEND:" + dtEnd + "\r\n"
                + "SUMMARY:Interview for " + application.getJob().getTitle() + "\r\n"
                + "LOCATION:" + escapeCalendarText(location) + "\r\n"
                + "DESCRIPTION:" + escapeCalendarText(description) + "\r\n"
                + "STATUS:CONFIRMED\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";
    }

    private String escapeCalendarText(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\n", "\\n").replace(",", "\\,").replace(";", "\\;");
    }

    public boolean sendApplicationSubmittedNotification(JobApplication application) {
        if (application == null || application.getCandidate() == null || application.getCandidate().getEmail() == null) {
            return false;
        }
        String to = application.getCandidate().getEmail();
        String subject = "Application Submitted: " + application.getJob().getTitle();
        String body = "Hello " + application.getCandidate().getName() + ",\n\n"
                + "Your application for the position '" + application.getJob().getTitle() + "' at " + application.getJob().getCompany() + " has been submitted successfully.\n"
                + "We will notify you when your application status changes.\n\n"
                + "Best regards,\nSmart Job Portal";
        return sendEmail(to, subject, body);
    }

    public boolean sendInterviewScheduledNotification(Interview interview) {
        if (interview == null || interview.getApplication() == null || interview.getApplication().getCandidate() == null) {
            return false;
        }
        String to = interview.getApplication().getCandidate().getEmail();
        String subject = "Interview Scheduled: " + interview.getApplication().getJob().getTitle();
        String body = "Hello " + interview.getApplication().getCandidate().getName() + ",\n\n"
                + "Your interview for the position '" + interview.getApplication().getJob().getTitle() + "' at "
                + interview.getApplication().getJob().getCompany() + " has been scheduled.\n\n"
                + "Date and time: " + interview.getScheduledAt() + "\n"
                + "Location: " + (interview.getLocation() == null ? "TBD" : interview.getLocation()) + "\n"
                + "Notes: " + (interview.getNotes() == null ? "None" : interview.getNotes()) + "\n\n"
                + "Good luck,\nSmart Job Portal";
        return sendEmail(to, subject, body);
    }

    public boolean sendApplicationStatusNotification(JobApplication application, ApplicationStatus status) {
        if (application == null || application.getCandidate() == null || application.getCandidate().getEmail() == null) {
            return false;
        }
        String statusLabel = switch (status) {
            case REVIEWED -> "under review";
            case INTERVIEW -> "moved to the interview stage";
            case OFFERED -> "updated with an offer";
            case SELECTED -> "selected";
            case REJECTED -> "not selected";
            default -> "updated";
        };
        String to = application.getCandidate().getEmail();
        String subject = "Application update: " + application.getJob().getTitle();
        String body = "Hello " + application.getCandidate().getName() + ",\n\n"
                + "Your application for '" + application.getJob().getTitle() + "' at " + application.getJob().getCompany() + " has been "
                + statusLabel + ".\n\n"
                + "Current status: " + status.name() + "\n\n"
                + "Thank you for applying. We will keep you informed of future updates.\n\n"
                + "Best regards,\nSmart Job Portal";
        return sendEmail(to, subject, body);
    }
}
