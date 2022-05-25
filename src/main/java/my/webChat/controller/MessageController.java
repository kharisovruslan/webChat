package my.webChat.controller;

import my.webChat.data.Message;
import my.webChat.data.User;
import my.webChat.service.ActiveUser;
import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("messages")
public class MessageController {
    @Autowired
    MessageService service;

    @Autowired
    ActiveUser activeUser;

    @Autowired
    UserService userService;

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @GetMapping("list")
    public String getMessagesList(@AuthenticationPrincipal User user,
                                  @RequestParam(value = "size") Optional<Integer> size,
                                  @RequestParam(value = "page") Optional<Integer> page,
                                  @RequestParam(value = "filter", required = false) Optional<String> filter,
                                  Model model) {
        int p = page.orElse(1);
        int s = size.orElse(5);
        String f = filter.orElse("");
        Page<Message> pageMessages;
        if (f.isEmpty()) {
            pageMessages = service.findAuthorMessageOrMessageForUser(user, PageRequest.of(p - 1, s));
        } else {
            pageMessages = service.findAuthorMessageOrMessageForUser(user, PageRequest.of(p - 1, s), f);
        }
        userService.updateVisited(user);
        activeUser.updateUser(user);
        model.addAttribute("userId", user.getId());
        model.addAttribute("filter", f);
        model.addAttribute("pageMessages", pageMessages);
        int totalPages = pageMessages.getTotalPages();
        List<Integer> pageNumbers = null;
        if ((totalPages > 0) && (totalPages <= 9)) {
            pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
        } else if (totalPages > 9) { // 1 ... 4 5 6 (7) 8 9 10 ... 15
            if (p < 5) {             // 1 2 3 (4) 5 6 7 ... 15
                pageNumbers = IntStream.rangeClosed(1, (p + 3)).boxed().collect(Collectors.toList());
                pageNumbers.add(-1);
                pageNumbers.add(totalPages);
            } else if (p >= (totalPages - 4)) {
                pageNumbers = IntStream.rangeClosed((p - 3), totalPages).boxed().collect(Collectors.toList());
                pageNumbers.add(0, -1);
                pageNumbers.add(0, 1);
            } else {
                pageNumbers = IntStream.rangeClosed((p - 3), p + 3).boxed().collect(Collectors.toList());
                pageNumbers.add(-1);
                pageNumbers.add(totalPages);
                pageNumbers.add(0, -1);
                pageNumbers.add(0, 1);
            }
        }
        model.addAttribute("pageNumbers", pageNumbers);
        return "fragments::messages";
    }

    @GetMapping("lastId")
    @ResponseBody
    public String lastID(@AuthenticationPrincipal User user) {
        activeUser.updateUser(user);
        return service.getLastID(user).toString();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("remove")
    public String messageRemove(@AuthenticationPrincipal User user, @RequestParam(name = "messageid") Long messageid) {
        service.removeMessage(messageid);
        return "redirect:/form";
    }

    @GetMapping(value = "file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> messageFile(@AuthenticationPrincipal User user,
                                              @RequestParam(name = "uuid") String uuid,
                                              @RequestParam(name = "fileName") String fileName) throws IOException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        String contentDisposition = "attachment; filename*=UTF-8''" + UriUtils.encodePath(fileName, "UTF-8");
        headers.set("Content-disposition", contentDisposition);
        return ResponseEntity.ok().headers(headers).body(service.sendFile(uuid));
    }
}
