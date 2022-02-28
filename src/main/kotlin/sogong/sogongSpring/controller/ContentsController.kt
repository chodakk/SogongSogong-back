package sogong.sogongSpring.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/")
class ContentsController {
    @GetMapping
    fun testpage():ResponseEntity<String>{
        val hello = "Hello!! API!! 다시 한번 테스트다..."
        val test = "음..."
        return ResponseEntity.ok(hello)
    }
}
