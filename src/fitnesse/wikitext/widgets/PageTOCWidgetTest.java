package fitnesse.wikitext.widgets;

import fitnesse.wiki.PagePointer;
import fitnesse.wiki.WikiPageDummy;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wikitext.WidgetBuilder;


public class PageTOCWidgetTest extends WidgetTestCase{
	
	@Override
	protected String getRegexp() {
	    return PageTOCWidget.REGEXP;
	 }

    public void testRegexp() throws Exception {
	    assertMatch("!pagecontents");
	}

	public WidgetRoot widgetRoot(String content) throws Exception
	{
		return new WidgetRoot(null, 
				new PagePointer(new WikiPageDummy("RooT",content), 
						new WikiPagePath()), 
				WidgetBuilder.htmlWidgetBuilder);
	}
	
	public void testFromMainLevel(){		
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("!1 Main title\n");
		pageContents.append("!2 Firt subtitle\n");
		pageContents.append("!2 Second subtitle\n");		
				
		try {
			
			PageTOCWidget toc= new PageTOCWidget(this.widgetRoot(pageContents.toString()), "");
			
			String render = unformattedText(toc);
			assertContains("<div class=\"toc1\">", render);
			assertContains("<div class=\"contents\">", render);
			assertContains("<b>Index:</b>", render);
			assertContains("<div class=\"toc1\">", render);
			
			
			assertContains("<ul><li><a href=\"#Main_title\">Main title</a></li><ul><li><a href=\"#Firt_subtitle\">Firt subtitle</a></li><li><a href=\"#Second_subtitle\">Second subtitle</a></li></ul></ul>", render);
						
			assertRef("Main_title","Main title", toc.render());
			assertRef("Firt_subtitle","Firt subtitle",toc.render());
			assertRef("Second_subtitle","Second subtitle",toc.render());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	

	public void testFromSecondLevel(){		
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("!2 First subtitle\n");
		pageContents.append("!2 Second subtitle\n");		
				
		try {			
			PageTOCWidget toc= new PageTOCWidget(this.widgetRoot(pageContents.toString()), "");
			String render = unformattedText(toc);
			assertContains("<div class=\"contents\">", render);
			assertContains("<ul><li><a href=\"#First_subtitle\">First subtitle</a></li><li><a href=\"#Second_subtitle\">Second subtitle</a></li></ul>", render);
			
			assertRef("First_subtitle","First subtitle", toc.render());
			assertRef("Second_subtitle","Second subtitle", toc.render());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testMultipleLevels(){		
		StringBuffer pageContents = new StringBuffer();
		pageContents.append("Text previous to first secction\n");
		pageContents.append("!1 Section A\n");
		pageContents.append("Text in section A\n");
		pageContents.append("!2 Section A.A\n");
		pageContents.append("Text in section A.A\n");
		pageContents.append("More text in section A.A\n");
		pageContents.append("!3 Section A.A.A\n");		
				
		try {			
			PageTOCWidget toc= new PageTOCWidget(this.widgetRoot(pageContents.toString()), "");
			String render = unformattedText(toc);
			assertContains("<div class=\"contents\">", render);
			
			StringBuffer listSTR= new StringBuffer();
			listSTR.append("<ul>");
			listSTR.append("<li><a href=\"#Section_A\">Section A</a></li>");
			listSTR.append("<ul>");
			listSTR.append("<li><a href=\"#Section_A.A\">Section A.A</a></li>");
			listSTR.append("<ul>");
			listSTR.append("<li><a href=\"#Section_A.A.A\">Section A.A.A</a></li>");
			listSTR.append("</ul>");
			listSTR.append("</ul>");
			listSTR.append("</ul>");
			
			assertContains(listSTR.toString(), render);		
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	

	private void assertContains(String expectedSubstring, String actual) {
		assertTrue("Expected substring \""+expectedSubstring+"\" not found in \""+actual+"\".", actual.contains(expectedSubstring));
	}

	private void assertRef(String linkText, String Name,String content) {
		String expected="<a href=\"#"+linkText+"\">"+Name+"</a>";
		assertTrue("Expected link \""+linkText+"\" not found in \""+content+"\".", content.contains(expected));
	}
	
	private String unformattedText(PageTOCWidget toc){
		String render;
		try {
			render = toc.render();
			String contentWithOutFormat=render.replaceAll("\t","");
			contentWithOutFormat=contentWithOutFormat.replaceAll("\n","");
			return contentWithOutFormat;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}
	
	

}
