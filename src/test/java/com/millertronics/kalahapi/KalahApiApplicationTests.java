package com.millertronics.kalahapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KalahApiApplicationTests {

	@LocalServerPort
	private int port;

	private static final String BASE_PATH = "http://localhost";

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders httpHeaders = new HttpHeaders();

	@Test
	@DisplayName("Should successfully create a Kalah game")
	public void testCreateGame() throws JsonProcessingException {
		ResponseEntity<String> response = postCreateGameRequest();
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

		JsonNode responseBody = new ObjectMapper().readTree(response.getBody());
		assertThat(responseBody.get("id").asText(), matchesPattern("\\d+"));
		assertThat(responseBody.get("url").asText(), equalTo("http://localhost:" + port + "/games"));
	}

	@Test
	@DisplayName("Should successfully process a move in a game")
	public void testMove() throws JsonProcessingException {

		ResponseEntity<String> createGame = postCreateGameRequest();
		final int gameId = Integer.parseInt(new ObjectMapper()
				.readTree(createGame.getBody())
				.get("id").asText());
		final int pitId = 1;

		final String uri = String.format("/games/%d/pits/%d", gameId, pitId);
		HttpEntity<String> request = new HttpEntity<>(null, httpHeaders);
		ResponseEntity<String> response =  restTemplate
				.exchange(generateFullUrl(uri), HttpMethod.PUT, request, String.class);

		JsonNode responseBody = new ObjectMapper().readTree(response.getBody());
		assertThat(responseBody.get("id").asText(), equalTo(String.valueOf(gameId)));

		String expectedUrl = String.format("http://localhost:%d/games/%d/pits/%d", port, gameId, pitId);
		assertThat(responseBody.get("url").asText(), equalTo(expectedUrl));

		String expectedStatus = "{" +
				"\"1\":\"0\"," +
				"\"2\":\"7\"," +
				"\"3\":\"7\"," +
				"\"4\":\"7\"," +
				"\"5\":\"7\"," +
				"\"6\":\"7\"," +
				"\"7\":\"1\"," +
				"\"8\":\"6\"," +
				"\"9\":\"6\"," +
				"\"10\":\"6\"," +
				"\"11\":\"6\"," +
				"\"12\":\"6\"," +
				"\"13\":\"6\"," +
				"\"14\":\"0\"" +
				"}";
		assertThat(responseBody.get("status").toString(), equalTo(expectedStatus));
	}

	private ResponseEntity<String> postCreateGameRequest() {
		HttpEntity<String> request = new HttpEntity<>(null, httpHeaders);
		return restTemplate.exchange(generateFullUrl("/games"), HttpMethod.POST, request, String.class);
	}

	private String generateFullUrl(String uri) {
		return String.format("%s:%s%s", BASE_PATH, port, uri);
	}

}
