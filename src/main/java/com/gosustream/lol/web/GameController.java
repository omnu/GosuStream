package com.gosustream.lol.web;
import com.gosustream.lol.domain.Game;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/games")
@Controller
@RooWebScaffold(path = "games", formBackingObject = Game.class)
public class GameController {
}
