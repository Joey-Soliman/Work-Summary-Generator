package com.zoom.restapi.controller;

import com.zoom.restapi.chat.GoogleDocs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleDocsController {

    @Autowired
    GoogleDocs googleDocs;

    @RequestMapping(path = "/createDoc", method = RequestMethod.GET)
    public String createDoc(@RequestParam(name = "id") String id) throws Exception {
        googleDocs.createDoc(id);
        return "Made Doc";
    }

}
