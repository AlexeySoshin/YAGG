package me.soshin.controller;


import me.soshin.fetcher.Fetcher;
import me.soshin.model.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

    @Autowired
    Fetcher fetcher;

    @RequestMapping("/graph")
    public Graph graph() {
        return fetcher.fetch();
    }
}
