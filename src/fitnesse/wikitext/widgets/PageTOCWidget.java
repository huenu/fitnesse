package fitnesse.wikitext.widgets;

import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.WikiWidget;

/**
 * This widget renders the hierarchical headers of the page as a TOC (table of
 * contents).
 * 
 * @author huenu
 * */
public class PageTOCWidget extends WikiWidget {

	public static final String REGEXP = "(?:^!pagecontents)";

	public PageTOCWidget(ParentWidget parent, String text) {
		super(parent);
	}

	@Override
	public String render() throws Exception {
		HtmlTag div = HtmlUtil.makeDivTag("toc1");
		HtmlTag contentsDiv = HtmlUtil.makeDivTag("contents");
		contentsDiv.add(HtmlUtil.makeBold("Index:"));
		HtmlTag toc = buildIndexContents(getWikiPage());
		if (toc != null)
			contentsDiv.add(toc);
		div.add(contentsDiv);
		return div.html();
	}

	private HtmlTag buildIndexContents(WikiPage wikiPage) throws Exception {
		HeaderParser parser = new HeaderParser();
		String pageContent = wikiPage.getData().getContent();
		return parser.parse(pageContent);
	}
}

class HeaderParser {
	private static final int MAX_DEPTH = 7;
	Boolean done = false;

	public HtmlTag parse(String content) {
		HtmlTag htmlT = processLevel(1, content);
		return htmlT;
	}

	private HtmlTag processLevel(int level, String levelString) {
		String[] parts = levelString.split("!" + level);

		if (parts.length == 1) {
			return (level < MAX_DEPTH) ? 
					this.processLevel(level + 1,levelString) 
					: null;
		} else {
			return this.processParts(level, parts);
		}
	}

	private HtmlTag processParts(int level, String[] parts) {
		HtmlTag list = new HtmlTag("ul");
		HtmlTag child = this.processLevel(level+1, parts[0]);
		if (child != null)
			list.add(child);
		
		for (int partnum = 1; partnum < parts.length; partnum++) {
			String part = parts[partnum];
 			if (part.length() > 0) {
				HtmlTag listItem = this.format(part);
				list.add(listItem);
				child = this.processLevel(level+1, part);
				if (child != null)
					list.add(child);
			}
		}
		return list;
	}

	/**
	 * Formats the first line of given string, to make a list item.
	 * */
	private HtmlTag format(String header) {
		HtmlTag listItem = new HtmlTag("li");
		int idxStop = (header.indexOf('\n') < 0) ? header.length() : header
				.indexOf('\n');
		String headerTitle = header.substring(0, idxStop).trim();
		try {
			HtmlTag link = HtmlUtil.makeLink("#"
					+ HeaderWidget.anchorName(headerTitle), headerTitle);
			listItem.add(link);
		} catch (Exception e) {
			// The link cannot be build
			listItem.add(headerTitle);
		}
		return listItem;
	}
}
