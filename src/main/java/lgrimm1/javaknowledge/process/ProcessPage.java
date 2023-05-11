package lgrimm1.javaknowledge.process;

import lgrimm1.javaknowledge.datamodels.HtmlContentAndReferences;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * @see #processTxt(List, String)
 */
@Component
public class ProcessPage {

	private final HtmlGenerators htmlGenerators;

	public ProcessPage(HtmlGenerators htmlGenerators) {
		this.htmlGenerators = htmlGenerators;
	}

	/**
	 * Processes the original text file lines and generates html code.
	 * Also, extracts page titles as references.
	 * Both Lists of String are wrapped up into a record.
	 */
	public HtmlContentAndReferences processTxt(List<String> text, String title) {
		List<String> html = htmlGenerators.generateFirstTags(title);
		HtmlContentAndReferences payload = htmlGenerators.generateMainContent(text);
		html.addAll(payload.content());
		html.addAll(htmlGenerators.generateLastTags());
		html = htmlGenerators.collectAndReferenceHeaders(html);
		return new HtmlContentAndReferences(html, payload.titles());
	}
}
