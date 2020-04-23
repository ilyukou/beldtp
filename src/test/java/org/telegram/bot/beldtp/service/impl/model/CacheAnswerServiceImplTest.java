package org.telegram.bot.beldtp.service.impl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.bot.beldtp.BeldtpApplication;
import org.telegram.bot.beldtp.TestConfig;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Import(TestConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BeldtpApplication.class)
@AutoConfigureMockMvc
class CacheAnswerServiceImplTest {

    @Autowired
    private CacheAnswerServiceImpl cacheAnswerService;

    @MockBean
    @Qualifier("not-cache")
    private AnswerService answerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private Answer notChange = new Answer();

    private Answer example1 = new Answer();
    private Answer example1v2 = new Answer();

    private Answer example2 = new Answer();

    private Answer newAnswer = new Answer();

    private Answer toRemove = new Answer();

    List<Answer> v1 = Arrays.asList(notChange,example1,example2, toRemove);
    List<Answer> v2 = Arrays.asList(notChange,example1v2, newAnswer);
    List<Answer> cache = Arrays.asList(notChange, example1v2, newAnswer, example2, toRemove);

    private static void removeMap(){
        CacheAnswerServiceImpl.setAnswers(new ConcurrentHashMap<>());
    }

    @BeforeEach
    void setUp() {

        removeMap();

        when(answerService.get(Language.EN)).thenReturn(v1);
        when(answerService.get(Language.RU)).thenReturn(new ArrayList<>());
        when(answerService.get(Language.BE)).thenReturn(new ArrayList<>());

        notChange.setLanguage(Language.EN);
        notChange.setType("notChange");

        example1.setLanguage(Language.EN);
        example1.setType("example1");

        example1v2.setLanguage(Language.EN);
        example1v2.setType("example1");

        example2.setLanguage(Language.EN);
        example2.setType("example2");

        newAnswer.setLanguage(Language.EN);
        newAnswer.setType("newAnswer");

        toRemove.setLanguage(Language.EN);
        toRemove.setType("toRemove");
    }

    @Test
    void get() throws InterruptedException {

        cacheAnswerService.update();

        for (Answer expected : v1){
            Answer found = this.cacheAnswerService.get(expected.getType(), expected.getLanguage());

            if(expected.getLanguage().equals(found.getLanguage()) && expected.getType().equals(found.getType())){
               // alright
            } else {
                fail();
            }
        }

        when(answerService.get(Language.EN)).thenReturn(v2);

        cacheAnswerService.update();

        for (Answer expected : cache){
            Answer found = this.cacheAnswerService.get(expected.getType(), expected.getLanguage());

            if(expected.getLanguage().equals(found.getLanguage()) && expected.getType().equals(found.getType())){
                // alright
            } else {
                fail();
            }
        }

    }

    @Test
    void testGet() {

        for (Answer answer : v1){
            List<Answer> list = cacheAnswerService.get(answer.getType());
            if(list.size() > 0){
                fail();
            }
        }

        cacheAnswerService.update();

        for (Answer answer : v1){
            List<Answer> list = cacheAnswerService.get(answer.getType());
            if(list == null || list.size() == 0){
                fail();
            }
        }
    }

    @Test
    void testGet1() {

        for (Answer answer : v1){
            if(cacheAnswerService.get(answer.getType(), answer.getLanguage()) != null){
                fail();
            }
        }

        cacheAnswerService.update();

        for (Answer answer : v1){
            if(cacheAnswerService.get(answer.getType(), answer.getLanguage()) == null){
                fail();
            }
        }
    }
}