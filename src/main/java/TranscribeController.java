import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
public class TranscribeController {

    private static final String BUCKET = "disability-aid-us-west2";

    @Autowired
    private S3Manager s3Ops;

    @Autowired
    private S3Transcription transcriber;

    //Spring MVC equivalent, HTTP GET correlates to GetMapping
    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/process")
    public String process() {
        return "process";
    }

    @GetMapping("/audio")
    public String audio() {
        return "upload";
    }

    @RequestMapping(value = "/getimages", method = RequestMethod.GET)
    @ResponseBody
    public String getImages(HttpServletRequest request, HttpServletResponse response) {
        return s3Ops.listAllBucketObjNames(BUCKET);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView singleFileUpload(@RequestParam("file") File file) {
        String name = file.getName();
        s3Ops.addAudioFile(BUCKET, name, file);

        return new ModelAndView(new RedirectView("audio-" + name));
    }


}
