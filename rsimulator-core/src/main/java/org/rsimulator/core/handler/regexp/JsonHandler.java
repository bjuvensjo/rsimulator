package org.rsimulator.core.handler.regexp;

import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * JsonHandler is a regular expression handler for json (.json).
 * 
 * @author Magnus Bjuvensjö
 * @see AbstractHandler
 */
@Singleton
public class JsonHandler extends AbstractHandler {
	private static final String EXTENSION = "json";
	private Logger log = LoggerFactory.getLogger(JsonHandler.class);
	private ObjectMapper mapper;

	public JsonHandler() {
		super();
		mapper = new ObjectMapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String escape(String request, boolean isCandidate) {
		String result = request;
		if (isCandidate && request != null && !"".equals(request)) {
			try {
				StringBuilder sb = new StringBuilder();
				JsonNode rootNode = mapper.readValue(request, JsonNode.class);
				escape(sb, rootNode, null);
				result = sb.toString();
			} catch (Exception e) {
				log.error(null, e);
			}			
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String format(String request) {
		String result = request;
		if (request != null && !"".equals(request)) {
			try {
				 JsonNode rootNode = mapper.readValue(request, JsonNode.class);
				 result = mapper.writeValueAsString(rootNode);
			} catch (Exception e) {
				log.error(null, e);
			}			
		}
		return result;
	}

	private void escape(StringBuilder sb, JsonNode node, String name) {
		if (node.isArray()) {
			sb.append("\\[");
			Iterator<JsonNode> iterator = node.iterator();
			int n = 0;
			while (iterator.hasNext()) {
				if (n > 0) {
					sb.append(",");
				}
				n++;
				escape(sb, iterator.next(), null);
			}
			sb.append("\\]");
		} else if (node.isObject()) {
			sb.append("\\{");
			Iterator<Entry<String, JsonNode>> fields = node.getFields();
			int n = 0;
			while (fields.hasNext()) {
				if (n > 0) {
					sb.append(",");
				}
				n++;
				Entry<String, JsonNode> field = fields.next();
				escape(sb, field.getValue(), field.getKey());
			}
			sb.append("\\}");
		} else {
			sb.append("\"").append(name).append("\":").append(node.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getExtension() {
		return EXTENSION;
	}
}