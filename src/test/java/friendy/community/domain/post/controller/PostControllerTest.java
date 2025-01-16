package friendy.community.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("포스트 생성 요청이 성공적으로 처리되면 201 Created와 함께 응답을 반환한다")
    public void createPostSuccessfullyReturns201Created() throws Exception {

        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest("this is new content");

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)  // 요청 본문이 JSON 형식임을 설정
                .content(objectMapper.writeValueAsString(postCreateRequest)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/posts/1"));

    }

    @Test
    @DisplayName("포스트 생성에 content가 없으면 400 Bad Request를 반환한다")
    void createPostWithoutContentReturns400BadRequest() throws Exception {

        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest(null);

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)  // 요청 본문이 JSON 형식임을 설정
                .content(objectMapper.writeValueAsString(postCreateRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("content가 2200자가 넘어가면 400 Bad Request를 반환한다")
    void createPostWithContentExceedingMaxLengthReturns400BadRequest() throws Exception {

        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest("Lasdasdsadasdasdasdasdadorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel libero nec erat tincidunt convallis ac nec eros. Aenean vel nisl et magna hendrerit aliquet. Fusce non vestibulum sem, eget facilisis nisl. Nulla facilisi. Nam pretium urna eu magna cursus, id fermentum dui eleifend. Fusce interdum sem et urna tempor, sed posuere ante vulputate. Cras viverra ante a lectus placerat, eu tristique erat ullamcorper. Sed euismod velit eget orci tempor, id malesuada arcu tristique. Donec sollicitudin mi id tincidunt consequat. Integer ut mauris magna. Integer efficitur mi libero, at placerat arcu tincidunt sed. Integer efficitur arcu non urna sodales convallis. Vivamus laoreet, elit nec luctus efficitur, enim ligula tincidunt ligula, vel posuere tortor nunc ut nunc. Aliquam erat volutpat. Aliquam vehicula dui ac dolor aliquet, ac condimentum libero egestas. Integer et est sed nulla varius tempus. Nullam nec dui ultricies, hendrerit purus eget, vulputate eros. Ut vitae placerat neque. Suspendisse lacinia, lectus et blandit consectetur, dui velit rhoncus justo, nec vehicula ante ligula at ligula. Cras tincidunt lorem ut turpis fermentum, sit amet elementum est convallis. Vivamus in egestas nulla. Aenean interdum ut ante vel convallis. Mauris vel viverra lorem, eget pretium ipsum. Donec aliquam malesuada orci, sit amet posuere augue iaculis sed. Vivamus suscipit dui nec lobortis eleifend. Vivamus id nisi non sapien porttitor consequat. Etiam nec velit leo. Aenean ut tempor ipsum. Donec fermentum, augue eu volutpat malesuada, dui neque fermentum magna, non tincidunt sapien tortor in tortor. Donec ut libero ac nunc vulputate dictum. Nam eget erat a ipsum aliquet malesuada. Nunc luctus erat nec efficitur sodales. Aliquam erat volutpat. In iaculis sapien sit amet purus posuere, eget fermentum dui gravida. Nam vulputate metus at lorem pretium, non posuere mi tempus. Aenean aliquam nunc nec lorem placerat, ac consectetur erat elementum. Nulla facilisi. Ut at leo ac metus placerat scelerisque. Cras feugiat, mauris at maximus suscipit, turpis metus faucibus odio, vel tristique libero tortor id tortor. Sed gravida nisi vitae urna viverra, nec vestibulum arcu pellentesque. Ut in tincidunt leo. Etiam pharetra sem eu risus aliquet, id placerat felis vestibulum. Nullam euismod, eros a faucibus tristique, risus arcu dictum risus, at suscipit purus libero vel odio. Nulla vel dictum eros, et sodales sapien. Nullam finibus auctor dui et tristique. Aliquam fringilla risus ac tortor mollis tincidunt. Vivamus posuere purus sit amet lectus tincidunt, sed tincidunt purus pharetra. Ut ullamcorper risus magna, vitae volutpat lorem placerat sit amet. Nam vel neque ac dui faucibus condimentum. Etiam iaculis, purus ac pretium lacinia, risus augue fermentum lorem, et volutpat urna odio eget sapien. Integer ut nibh in libero pretium lobortis. Cras ut ex neque. Duis at vulputate purus. Nam molestie neque at maximus feugiat. Vivamus at sollicitudin erat, et feugiat odio. Curabitur tempor augue dui, ut iaculis arcu varius ac. Curabitur lobortis felis libero, ac hendrerit augue faucibus at. Suspendisse potenti. Integer tristique venenatis vestibulum. Nam cursus eros purus, id vehicula eros rhoncus non. Curabitur volutpat ligula non mi iaculis, sed tincidunt lorem malesuada. Fusce vehicula felis ac est congue, eget placerat ligula elementum. Vivamus et purus id est pharetra faucibus ut id arcu. Nam ac nulla eget urna sodales elementum. Sed vulputate orci vel velit sollicitudin, in fringilla felis interdum. Nulla et est vehicula, dignissim nunc ut, feugiat enim. Fusce congue magna sit amet gravida fermentum. Vivamus et ante vel odio malesuada bibendum. Phasellus venenatis, ipsum sit amet lacinia finibus, orci erat tincidunt nunc, ac eleifend nisi justo non leo. Curabitur euismod fermentum arcu, a viverra odio bibendum sed. Nunc ut felis leo. Fusce consequat leo eget magna vehicula, vel viverra mi bibendum. Aenean sit amet lorem dolor. Proin vel tincidunt enim. Sed sollicitudin viverra ante eu mollis. Pellentesque ac arcu id lacus tincidunt volutpat. Etiam tristique felis velit, ac vulputate neque fermentum vel. Mauris euismod libero erat, vel laoreet risus pharetra ac. Nunc sollicitudin vel turpis a malesuada. Ut in facilisis dui. Aliquam sollicitudin lobortis lectus, ac tempor lorem volutpat et. Nunc aliquam diam non ligula gravida maximus. Integer at metus ac odio placerat consectetur non vitae augue. In non libero vel lectus elementum volutpat. Integer euismod, sapien non ullamcorper placerat, nunc augue efficitur nunc, eu tincidunt ante felis a orci. Donec fringilla felis quis mauris pretium, at dignissim sapien efficitur. Mauris lacinia suscipit leo, sit amet laoreet erat elementum ac.\n");

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)  // 요청 본문이 JSON 형식임을 설정
                .content(objectMapper.writeValueAsString(postCreateRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

}