package com.github.jamorin.bravia;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableZuulProxy
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate clientRestTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public MultiValueMap<String, String> soapHeaders(ApplicationProperties appProperties) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("X-Auth-PSK", appProperties.getPreSharedKey());
		headers.add("SOAPACTION", "\"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\"");
		headers.add("Content-Type", "text/xml; charset=UTF-8");
		return headers;
	}

	@Bean
	public Map<String, String> cmdSlugs(ApplicationProperties appProperties) {
		log.info("Mapping {} tv commands", appProperties.getCommand().size());
		Map<String, String> cmdSlugs = new HashMap<>();
		for (val entry : appProperties.getCommand().entrySet()) {
			cmdSlugs.put(Utils.slug(entry.getKey()), entry.getValue());
		}
		return cmdSlugs;
	}

}
