package ru.bicev.ReportGenerator;

import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class ReportGeneratorApplicationTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	String json = """
						{
			  "reportId": "test-id",
			  "data": [
			    {
			      "type": "report",
			      "name": "Alice",
			      "date": "2025-07-15",
			      "amount": 1200.50
			    },
			    {
			      "type": "report",
			      "name": "Bob",
			      "date": "2025-07-14",
			      "amount": 850.00
			    },
			    {
			      "type": "report",
			      "name": "Tom",
			      "date": "2025-07-13",
			      "amount": 50.00
			    },
			    {
			      "type": "report",
			      "name": "Lisa",
			      "date": "2024-03-02",
			      "amount": 212850.00
			    }
			  ]
			}

						""";

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void shouldSubmitReportSuccessfully() throws Exception {

		mockMvc.perform(put("/api/reports")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isAccepted());
	}

	@Test
	void shouldReturnStatusSuccessfully() throws Exception {
		mockMvc.perform(put("/api/reports")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isAccepted());

		Thread.sleep(2000);

		mockMvc.perform(get("/api/reports/test-id"))
				.andExpect(status().isOk())
				.andExpect(content().string("\"DONE\""));
	}

	@Test
	void shouldReturnNotFoundWhenIdIsInvalid() throws Exception {
		mockMvc.perform(get("/api/reports/notvalid-id"))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldDownloadFile() throws Exception {
		mockMvc.perform(put("/api/reports")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isAccepted());

		Thread.sleep(4000);

		MvcResult result = mockMvc.perform(get("/api/reports/test-id/download"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", Matchers.containsString("attachment")))
				.andReturn();

		byte[] responseContent = result.getResponse().getContentAsByteArray();
		assertFalse(responseContent.length == 0);
	}

}
