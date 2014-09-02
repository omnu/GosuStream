// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.web;

import com.gosustream.lol.domain.Game;
import com.gosustream.lol.web.GameController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect GameController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String GameController.create(@Valid Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, game);
            return "games/create";
        }
        uiModel.asMap().clear();
        game.persist();
        return "redirect:/games/" + encodeUrlPathSegment(game.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String GameController.createForm(Model uiModel) {
        populateEditForm(uiModel, new Game());
        return "games/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String GameController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("game", Game.findGame(id));
        uiModel.addAttribute("itemId", id);
        return "games/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String GameController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("games", Game.findGameEntries(firstResult, sizeNo));
            float nrOfPages = (float) Game.countGames() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("games", Game.findAllGames());
        }
        addDateTimeFormatPatterns(uiModel);
        return "games/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String GameController.update(@Valid Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, game);
            return "games/update";
        }
        uiModel.asMap().clear();
        game.merge();
        return "redirect:/games/" + encodeUrlPathSegment(game.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String GameController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Game.findGame(id));
        return "games/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String GameController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Game game = Game.findGame(id);
        game.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/games";
    }
    
    void GameController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("game_starttime_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("game_endtime_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
    
    void GameController.populateEditForm(Model uiModel, Game game) {
        uiModel.addAttribute("game", game);
        addDateTimeFormatPatterns(uiModel);
    }
    
    String GameController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
