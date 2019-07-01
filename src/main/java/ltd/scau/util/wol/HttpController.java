package ltd.scau.util.wol;

import ltd.scau.util.wol.net.WakeOnLan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Wu Weijie
 */
@RestController
public class HttpController {

    @GetMapping("/wake-up")
    public String wakeUp() throws IOException {
        WakeOnLan.wakeAll();
        return "{}";
    }
}
