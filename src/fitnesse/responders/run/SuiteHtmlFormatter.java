// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.responders.run;

import fitnesse.html.HtmlPageFactory;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;

public abstract class SuiteHtmlFormatter extends TestHtmlFormatter {

  private static final String cssSuffix1 = "1";
  private static final String cssSuffix2 = "2";

  private TestSummary pageCounts = new TestSummary();
  private static final String TEST_SUMMARIES_ID = "test_summaries";

  private String cssSuffix = cssSuffix1;
  private int pageNumber = 0;
  private boolean firstTest = true;
  private String testSystemFullName = null;


  public SuiteHtmlFormatter(WikiPage page, HtmlPageFactory pageFactory) throws Exception {
    super(page, pageFactory);
  }

  public String getTestSystemHeader(String testSystemName) throws Exception {
    String tag = String.format("<h3>%s</h3>\n", testSystemName);
    HtmlTag insertScript = HtmlUtil.makeAppendElementScript("test_summaries", tag);
    return insertScript.html();
  }
  
  public void announceStartNewTest(String relativeName, String fullPathName) throws Exception {
    pageNumber++;
    
    if (firstTest) {
      HtmlTag outputTitle = new HtmlTag("h2", "Test Output");
      outputTitle.addAttribute("class", "centered");
      writeData(outputTitle.html());
      firstTest = false;
    }
    
    if (testSystemFullName != null) {
      HtmlTag systemTitle = new HtmlTag("h2", String.format("Test System: %s", testSystemFullName));
      systemTitle.addAttribute("class", "centered");
      writeData(systemTitle.html());
      // once we write it out we don't need it any more
      testSystemFullName = null;
    }
    
    HtmlTag pageNameBar = HtmlUtil.makeDivTag("test_output_name");
    HtmlTag anchor = HtmlUtil.makeLink(fullPathName, relativeName);
    anchor.addAttribute("id", relativeName + pageNumber);
    pageNameBar.add(anchor);
    writeData(pageNameBar.html());
    
    writeData("<div class=\"alternating_block_" + cssSuffix + "\">");
  }
  
  @Override
  public void announceStartNewTest(WikiPage newTest) throws Exception {
    PageCrawler pageCrawler = getPage().getPageCrawler();
    String relativeName = pageCrawler.getRelativeName(getPage(), newTest);
    WikiPagePath fullPath = pageCrawler.getFullPath(newTest);
    String fullPathName = PathParser.render(fullPath);
    
    announceStartNewTest(relativeName, fullPathName);
  }
  
  @Override
  public void processTestOutput(String output) throws Exception {
    writeData(output);
  }
  
  public void processTestResults(String relativeName, TestSummary testSummary) throws Exception {
    finishOutputForTest();
    
    getAssertionCounts().tally(testSummary);
    
    switchCssSuffix();
    HtmlTag mainDiv = HtmlUtil.makeDivTag("alternating_row_" + cssSuffix);

    mainDiv.add(HtmlUtil.makeSpanTag("test_summary_results " + cssClassFor(testSummary), testSummary.toString()));

    HtmlTag link = HtmlUtil.makeLink("#" + relativeName + pageNumber, relativeName);
    link.addAttribute("class", "test_summary_link");
    mainDiv.add(link);

    pageCounts.tallyPageCounts(testSummary);
    HtmlTag insertScript = HtmlUtil.makeAppendElementScript(TEST_SUMMARIES_ID, mainDiv.html(2));
    writeData(insertScript.html());
  }
  
  private void finishOutputForTest() throws Exception {
    writeData("</div>" + HtmlTag.endl);
  }
  
  @Override
  public void processTestResults(WikiPage testPage, TestSummary testSummary)
      throws Exception {
    PageCrawler pageCrawler = getPage().getPageCrawler();
    String relativeName = pageCrawler.getRelativeName(getPage(), testPage);
    if ("".equals(relativeName)) {
      relativeName = String.format("(%s)", testPage.getName());
    }
    
    processTestResults(relativeName, testSummary);
  }

  private void switchCssSuffix() {
    if (cssSuffix1.equals(cssSuffix))
      cssSuffix = cssSuffix2;
    else
      cssSuffix = cssSuffix1;
  }
  
  @Override
  public void announceStartTestSystem(TestSystem testSystem, String testSystemName, String testRunner)
      throws Exception {
    String tag = String.format("<h3>%s</h3>\n", testSystemName  + ":" + testRunner);
    HtmlTag insertScript = HtmlUtil.makeAppendElementScript(TEST_SUMMARIES_ID, tag);
    writeData(insertScript.html());
    
    testSystemFullName = testSystemName + ":" + testRunner;
  }
  
  @Override
  protected String makeSummaryContent() {
    String testPagesSummary = "<strong>Test Pages:</strong> " + pageCounts.toString() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    return testPagesSummary + super.makeSummaryContent();
  }

  public void finishWritingOutput() throws Exception {
    writeData(testSummary());
    writeData(getHtmlPage().postDivision);
  }
  
  @Override
  public void writeHead(String pageType) throws Exception {
    super.writeHead(pageType);
    
    HtmlTag outputTitle = new HtmlTag("h2", "Test Summaries");
    outputTitle.addAttribute("class", "centered");
    writeData(outputTitle.html());
    HtmlTag summariesDiv = HtmlUtil.makeDivTag(TEST_SUMMARIES_ID);
    summariesDiv.addAttribute("id", TEST_SUMMARIES_ID);
    writeData(summariesDiv.html());
  }
}



