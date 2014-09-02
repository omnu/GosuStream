// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.web;

import com.gosustream.lol.domain.LiveGame;
import com.gosustream.lol.web.LiveGameController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect LiveGameController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String LiveGameController.create(@Valid LiveGame liveGame, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liveGame);
            return "livegames/create";
        }
        uiModel.asMap().clear();
        liveGame.persist();
        return "redirect:/livegames/" + encodeUrlPathSegment(liveGame.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String LiveGameController.createForm(Model uiModel) {
        populateEditForm(uiModel, new LiveGame());
        return "livegames/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String LiveGameController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("livegame", LiveGame.findLiveGame(id));
        uiModel.addAttribute("itemId", id);
        return "livegames/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String LiveGameController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("livegames", LiveGame.findLiveGameEntries(firstResult, sizeNo));
            float nrOfPages = (float) LiveGame.countLiveGames() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("livegames", LiveGame.findAllLiveGames());
        }
        addDateTimeFormatPatterns(uiModel);
        return "livegames/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String LiveGameController.update(@Valid LiveGame liveGame, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liveGame);
            return "livegames/update";
        }
        uiModel.asMap().clear();
        liveGame.merge();
        return "redirect:/livegames/" + encodeUrlPathSegment(liveGame.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String LiveGameController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, LiveGame.findLiveGame(id));
        return "livegames/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String LiveGameController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        LiveGame liveGame = LiveGame.findLiveGame(id);
        liveGame.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/livegames";
    }
    
    void LiveGameController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("liveGame_starttime_date_format", "yyyy-MM-dd HH-mm-ss Z");
    }
    
    void LiveGameController.populateEditForm(Model uiModel, LiveGame liveGame) {
        uiModel.addAttribute("liveGame", liveGame);
        addDateTimeFormatPatterns(uiModel);
    }
    
    String LiveGameController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
