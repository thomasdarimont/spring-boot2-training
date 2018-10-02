package quiz;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/game")
class GameController {

    private final HttpServletRequest request;

    public GameController(HttpServletRequest request) {
        this.request = request;
    }

    @GetMapping("/new")
    String newGame(@RequestParam(defaultValue = "100") int max) {

        HttpSession currentSession = request.getSession();
        currentSession.invalidate();

        Game game = new Game(1, max);
        game.start();

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("game", game);

        return "redirect:/game";
    }

    @GetMapping
    String game(Model model) {

        Game game = getGame();
        model.addAttribute("game", game);

        return "game";
    }

    @PostMapping
    String guess(int guess) {

        Game game = getGame();
        game.guess(guess);

        return "redirect:/game";
    }

    private Game getGame() {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        return (Game) session.getAttribute("game");
    }
}
