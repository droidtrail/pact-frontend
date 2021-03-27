package pact.consumer;

import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import br.ce.wcaquino.tasksfrontend.model.Todo;
import br.ce.wcaquino.tasksfrontend.repositories.TasksRepository;

public class SaveInvalidTaskTest2 {
	
	@Rule
	public PactProviderRule mockProvider = new PactProviderRule("Tasks", this);

	@Pact(consumer = "TasksFront")
	public RequestResponsePact createPact(PactDslWithProvider builder) {
		
		DslPart requestBody = new PactDslJsonBody()
				.nullValue("id")
				.nullValue("task")
				.nullValue("dueDate");
			
		DslPart responseBody = new PactDslJsonBody()
				.numberType("id")
				.stringType("message","Fill the task description");
		
		return builder
				.uponReceiving("Save an invalid task")
					.path("/todo")
					.method("POST")
					.matchHeader("Content-type", "application/json.*","application/json")
					.body(requestBody)
			    .willRespondWith()
			    	.status(400)
			    	.body(responseBody)
			    .toPact();
	}
	
	@Test
	@PactVerification
	public void test() throws ClientProtocolException, IOException {
		//Arrange
		TasksRepository consumer = new TasksRepository(mockProvider.getUrl());
		
		//Act
		try {
			consumer.save(new Todo(null,null,null));
			Assert.fail("Should throws an exception");
		} catch (Exception ex) {
			
			System.out.println(ex.getMessage());
			assertThat(ex.getMessage(), CoreMatchers.containsString("400 Bad Request"));
			assertThat(ex.getMessage(), CoreMatchers.containsString("Fill the task description"));
			
		}
		
	}

}
