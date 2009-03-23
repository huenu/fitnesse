package fitnesse.wikitext.widgets;

import fitnesse.wiki.PagePointer;
import fitnesse.wiki.WikiPageDummy;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wikitext.WidgetBuilder;

public class PageTOCWidgetTest extends WidgetTestCase {

	@Override
	protected String getRegexp() {
		return PageTOCWidget.REGEXP;
	}

	public void testRegexp() throws Exception {
		assertMatch("!pagecontents");
	}

	public WidgetRoot widgetRoot(String content) throws Exception {
		return new WidgetRoot(null, new PagePointer(new WikiPageDummy("RooT",
				content), new WikiPagePath()), WidgetBuilder.htmlWidgetBuilder);
	}

	public void testEmptyPage() {
		StringBuffer pageContents = new StringBuffer();
		try {
			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");

			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	

	public void testPageWithSimpleText() {
		StringBuffer pageContents = new StringBuffer("This page has only '''simple''' text");
		try {
			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");

			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	

	public void testFromMainLevel() {
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("!1 Main title\n");
		pageContents.append("!2 Firt subtitle\n");
		pageContents.append("!2 Second subtitle\n");

		try {

			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");

			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);

			assertContains(
					"<ul><li><a href=\"#Main_title\">Main title</a></li><ul><li><a href=\"#Firt_subtitle\">Firt subtitle</a></li><li><a href=\"#Second_subtitle\">Second subtitle</a></li></ul></ul>",
					render);

			assertRef("Main_title", "Main title", toc.render());
			assertRef("Firt_subtitle", "Firt subtitle", toc.render());
			assertRef("Second_subtitle", "Second subtitle", toc.render());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}


	public void testMissingLevel() {
		StringBuffer pageContents = new StringBuffer();
		
		pageContents.append("!1 Main title\n");
		pageContents.append("!2 Firt subtitle\n");
		pageContents.append("!1 Other Main title\n");
		pageContents.append("!3 subtitle of level 3\n");
		pageContents.append("!5 subsubtitle of level 5\n");

		try {

			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");

			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);

			assertContains(
					"<ul><li><a href=\"#Main_title\">Main title</a></li>" +
					"<ul><li><a href=\"#Firt_subtitle\">Firt subtitle</a></li></ul>" +
					"<li><a href=\"#Other_Main_title\">Other Main title</a></li>" +
					"<ul><li><a href=\"#subtitle_of_level_3\">subtitle of level 3</a></li>" +
					"<ul><li><a href=\"#subsubtitle_of_level_5\">subsubtitle of level 5</a></li>" +
					"</ul></ul></ul>",
					render);
			String renderedText=toc.render();
			assertRef("Main_title", "Main title", renderedText);
			assertRef("Firt_subtitle", "Firt subtitle",renderedText);
			assertRef("subtitle_of_level_3", "subtitle of level 3", renderedText);

		} catch (Exception e) {
			fail(e.getMessage());		
		}
		}
	


	public void testMissingLevel2() {
		StringBuffer pageContents = new StringBuffer();
		
		pageContents.append("!1 Main title\n");
		pageContents.append("!2 Firt subtitle\n");
		pageContents.append("!4 subsubtitle level 4\n");
		pageContents.append("!2 second subtitle\n");
		pageContents.append("!1 Other Main title\n");
		pageContents.append("!3 subtitle of level 3\n");
		pageContents.append("!5 subsubtitle of level 5\n");
		pageContents.append("!4 subsubtitle of level 4\n");
		pageContents.append("!5 subsubsubtitle of level 5\n");

		try {

			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");

			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);

			

			assertContains(
					"<li><a href=\"#Main_title\">Main title</a></li>" +
					"<ul>" +
						"<li><a href=\"#Firt_subtitle\">Firt subtitle</a></li>" +
						"<ul><li><a href=\"#subsubtitle_level_4\">subsubtitle level 4</a></li></ul>" +
						"<li><a href=\"#second_subtitle\">second subtitle</a></li>" +
					"</ul>",render); 
			assertContains(
					"<li><a href=\"#Other_Main_title\">Other Main title</a></li>" +
					"<ul>" +
						"<li><a href=\"#subtitle_of_level_3\">subtitle of level 3</a></li>" +
						"<ul>" +
							"<ul>" +
								"<li><a href=\"#subsubtitle_of_level_5\">subsubtitle of level 5</a></li>" +
							"</ul>" +
							"<li><a href=\"#subsubtitle_of_level_4\">subsubtitle of level 4</a></li>" +
								"<ul><li><a href=\"#subsubsubtitle_of_level_5\">subsubsubtitle of level 5</a></li></ul>" +
							"</ul>" +
						"</ul>" +
					"</ul>",
					render);
			
			String renderedText=toc.render();
			assertRef("Main_title", "Main title", renderedText);
			assertRef("Firt_subtitle", "Firt subtitle",renderedText);
			assertRef("subtitle_of_level_3", "subtitle of level 3", renderedText);

		} catch (Exception e) {
			fail(e.getMessage());		
		}
		}
	
	public void testNoLineBreak() {
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("!2 Sub Title");

		try {

			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");

			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);
			assertContains("<div class=\"toc1\">", render);

			assertContains(
					"<ul>" + "<li><a href=\"#Sub_Title\">Sub Title</a></li>"
							+ "</ul>", render);

			assertRef("Sub_Title", "Sub Title", toc.render());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testFromSecondLevel() {
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("!2 First subtitle\n");
		pageContents.append("!2 Second subtitle\n");

		try {
			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");
			String render = unformattedText(toc);
			assertContains("<div class=\"contents\">", render);
			assertContains(
					"<ul><li><a href=\"#First_subtitle\">First subtitle</a></li><li><a href=\"#Second_subtitle\">Second subtitle</a></li></ul>",
					render);

			assertRef("First_subtitle", "First subtitle", toc.render());
			assertRef("Second_subtitle", "Second subtitle", toc.render());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testMultipleLevels() {
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("Text previous to first secction\n");
		pageContents.append("!1 Section A\n");
		pageContents.append("Text in section A\n");
		pageContents.append("!2 Section A.A\n");
		pageContents.append("Text in section A.A\n");
		pageContents.append("More text in section A.A\n");
		pageContents.append("!3 Section A.A.A\n");

		try {
			PageTOCWidget toc = new PageTOCWidget(this.widgetRoot(pageContents
					.toString()), "");
			String render = unformattedText(toc);
			assertContains("<div class=\"contents\">", render);

			StringBuffer listSTR = new StringBuffer();
			listSTR.append("<ul>");
			listSTR.append("<li><a href=\"#Section_A\">Section A</a></li>");
			listSTR.append("<ul>");
			listSTR.append("<li><a href=\"#Section_A.A\">Section A.A</a></li>");
			listSTR.append("<ul>");
			listSTR
					.append("<li><a href=\"#Section_A.A.A\">Section A.A.A</a></li>");
			listSTR.append("</ul>");
			listSTR.append("</ul>");
			listSTR.append("</ul>");

			assertContains(listSTR.toString(), render);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private void assertContains(String expectedSubstring, String actual) {
		assertTrue("Expected substring \"" + expectedSubstring
				+ "\" not found in \"" + actual + "\".", actual
				.contains(expectedSubstring));
	}

	private void assertRef(String linkText, String Name, String content) {
		String expected = "<a href=\"#" + linkText + "\">" + Name + "</a>";
		assertTrue("Expected link \"" + linkText + "\" not found in \""
				+ content + "\".", content.contains(expected));
	}

	private String unformattedText(PageTOCWidget toc) throws Exception {
		String render = toc.render();
		String contentWithOutFormat = render.replaceAll("\t", "");
		contentWithOutFormat = contentWithOutFormat.replaceAll("\n", "");
		return contentWithOutFormat;
	}

}