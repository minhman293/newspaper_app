package com.man293.readnewspaperapp.activities;

import android.text.Html;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLDOMParser {

    public Document getDocument(String xml) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            is.setEncoding("UTF-8");
            document = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        }
        return document;
    }

    public String getValue(Element item, String name) {
        NodeList nodes = item.getElementsByTagName(name);
        Element element = (Element) nodes.item(0);

        if (element != null) {
            // Check if the element contains CDATA
            Node cdataNode = element.getFirstChild();
            if (cdataNode != null && cdataNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                // If it's a CDATA section, extract the CDATA content
                return Html.fromHtml(cdataNode.getNodeValue(), Html.FROM_HTML_MODE_LEGACY).toString();
            } else if (cdataNode != null && cdataNode.getNodeType() == Node.TEXT_NODE) {
                // If it's a regular text node, return the node value
                return Html.fromHtml(cdataNode.getNodeValue(), Html.FROM_HTML_MODE_LEGACY).toString();
            }
        }
        return "";
    }

    private final String getTextNodeValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public String getImageSrcFromDescription(Element item) {
        NodeList descriptionNodes = item.getElementsByTagName("description");
        if (descriptionNodes.getLength() > 0) {
            Element descriptionElement = (Element) descriptionNodes.item(0);
            Node cdataNode = descriptionElement.getFirstChild();

            if (cdataNode != null && cdataNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                String cdataContent = cdataNode.getNodeValue();

                // Use regular expressions to extract the img src attribute
                Pattern pattern = Pattern.compile("<img\\s+[^>]*src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Matcher matcher = pattern.matcher(cdataContent);

                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }

        return "";
    }

    public Date getDateFromPubDate(Element item) {
        NodeList pubDateNodes = item.getElementsByTagName("pubDate");
        if (pubDateNodes.getLength() > 0) {
            Element pubDateElement = (Element) pubDateNodes.item(0);
            String pubDateStr = pubDateElement.getTextContent();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yy HH:mm:ss Z", Locale.ENGLISH);
                Date date = sdf.parse(pubDateStr);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getDescriptionContent(Element item) {
        NodeList descriptionNodes = item.getElementsByTagName("description");
        if (descriptionNodes.getLength() > 0) {
            Element descriptionElement = (Element) descriptionNodes.item(0);
            Node cdataNode = descriptionElement.getFirstChild();

            if (cdataNode != null && cdataNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                String cdataContent = cdataNode.getNodeValue();

                // Extract text content from the CDATA section
                String description = extractTextFromCDATA(cdataContent);

                return description;
            }
        }

        return "";
    }

    private String extractTextFromCDATA(String cdataContent) {
        StringBuilder extractedText = new StringBuilder();
        boolean inTag = false;

        for (char c : cdataContent.toCharArray()) {
            if (c == '<') {
                inTag = true;
            } else if (c == '>') {
                inTag = false;
            } else if (!inTag) {
                extractedText.append(c);
            }
        }

        // Replace HTML entities with their corresponding characters
        String description = Html.fromHtml(extractedText.toString(), Html.FROM_HTML_MODE_LEGACY).toString();

        return description;
    }

}