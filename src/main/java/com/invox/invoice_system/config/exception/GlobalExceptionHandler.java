package com.invox.invoice_system.config.exception;

import com.invox.invoice_system.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Import MediaType
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView; // Import ModelAndView

import java.time.LocalDateTime;
import java.util.List; // Import List

@ControllerAdvice
public class GlobalExceptionHandler {

    // Phương thức tiện ích để kiểm tra xem client có ưu tiên HTML không
    private boolean prefersHtml(WebRequest request) {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader == null) {
            return false; // Mặc định không phải HTML nếu không có Accept header
        }
        // Chuyển đổi sang List<MediaType> để dễ dàng kiểm tra
        try {
            List<MediaType> mediaTypes = MediaType.parseMediaTypes(acceptHeader);
            for (MediaType mediaType : mediaTypes) {
                if (mediaType.isCompatibleWith(MediaType.TEXT_HTML)) {
                    return true;
                }
                // Nếu application/json hoặc application/*+json xuất hiện trước text/html với q cao hơn,
                // thì có thể không ưu tiên HTML. Tuy nhiên, kiểm tra đơn giản isCompatibleWith(TEXT_HTML)
                // thường là đủ cho các trình duyệt.
            }
        } catch (IllegalArgumentException ex) {
            // Bỏ qua nếu Accept header không hợp lệ
        }
        return false;
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String path = getRequestPath(request);
        ex.printStackTrace(); // In ra để debug

        if (prefersHtml(request)) {
            ModelAndView mav = new ModelAndView();
            mav.addObject("timestamp", LocalDateTime.now().toString());
            mav.addObject("status", HttpStatus.BAD_REQUEST.value());
            mav.addObject("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            mav.addObject("message", ex.getMessage());
            mav.addObject("path", path);
            // Giả sử bạn có một template tên là "error-general.html" trong templates/error/
            mav.setViewName("error/error-general"); 
            return mav;
        } else {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    ex.getMessage(),
                    path
            );
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(Exception.class)
    public Object handleGlobalException(Exception ex, WebRequest request) {
        String path = getRequestPath(request);
        ex.printStackTrace(); // Rất quan trọng: In lỗi gốc ra console để debug!

        if (prefersHtml(request)) {
            ModelAndView mav = new ModelAndView();
            mav.addObject("timestamp", LocalDateTime.now().toString());
            mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            mav.addObject("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            mav.addObject("message", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.");
            // Bạn có thể muốn hiển thị ex.getMessage() ở đây trong môi trường dev
            // mav.addObject("exceptionMessage", ex.getMessage()); 
            mav.addObject("path", path);
            mav.setViewName("error/error-general"); // Template lỗi chung
            return mav;
        } else {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.",
                    path
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}