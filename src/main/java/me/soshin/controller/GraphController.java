package me.soshin.controller;


import me.soshin.fetcher.Fetcher;
import me.soshin.model.Graph;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class GraphController {

    @Resource(name = "github_events")
    Fetcher fetcher;

    @RequestMapping("/graph")
    public Graph graph() {
        return this.fetcher.fetch();
    }
}
