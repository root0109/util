/**
 * 
 */
package com.zaprit.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author vaibhav.singh
 *
 */
@Data
@Component
public class CacheProperties
{
	@Value("${cache.enabled:false}")
	private boolean enabled;

	@Value("${cache.name:DEFAULT}")
	private String name;

	@Value("${cache.timeout:3600}")
	private int timeout;

	@Value("${cache.default-ttl:3600}")
	private int defaultTtl;

	@Value("${cache.servers:}")
	private String servers;
}
