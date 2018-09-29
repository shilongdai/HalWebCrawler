package net.viperfish.crawlerApp.xmlDao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicLong;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.Anchor;
import net.viperfish.crawler.html.EmphasizedTextContent;
import net.viperfish.crawler.html.Header;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TextContent;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLDataSink implements Datasink<Site> {

	private File outDir;
	private AtomicLong currentID;
	private boolean closed;

	public XMLDataSink(String dirName) {
		outDir = new File(dirName);
		currentID = new AtomicLong(0);
		closed = false;
	}

	public void init() throws IOException {
		if (outDir.exists()) {
			Files.walkFileTree(outDir.toPath(), new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}
		Files.createDirectory(outDir.toPath(),
			PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---")));
	}

	@Override
	public void write(Site data) throws IOException {
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		File toWrite = Files.createFile(
			Paths.get(outDir.getCanonicalPath(), Long.toString(currentID.getAndIncrement())))
			.toFile();
		Document siteDoc = site2XML(data);

		try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(toWrite))) {
			xmlOutputter.output(siteDoc, out);
		}
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		closed = true;
	}

	private Document site2XML(Site in) {
		Element siteRoot = new Element("site");
		Element checksum = new Element("checksum");
		Element compressed = new Element("compressedHTML");
		Element title = new Element("title");
		Element url = new Element("url");

		checksum.setContent(new Text(in.getChecksum()));
		compressed.setContent(new Text(Base64.getEncoder().encodeToString(in.getCompressedHtml())));
		title.setContent(new CDATA(in.getTitle()));
		url.setContent(new CDATA(in.getUrl().toExternalForm()));

		siteRoot.addContent(title);
		siteRoot.addContent(checksum);
		siteRoot.addContent(compressed);
		siteRoot.addContent(url);

		for (Anchor a : in.getAnchors()) {
			siteRoot.addContent(anchor2Element(a));
		}

		for (Header h : in.getHeaders()) {
			siteRoot.addContent(header2Element(h));
		}

		for (TextContent t : in.getTexts()) {
			siteRoot.addContent(text2Element(t));
		}

		for (EmphasizedTextContent e : in.getEmphasizedTexts()) {
			siteRoot.addContent(emphasizedText2Element(e));
		}

		Document result = new Document(siteRoot);
		return result;
	}

	private Element anchor2Element(Anchor anchor) {
		Element result = new Element("anchor");
		Element anchorText = new Element("text");
		Element targetURL = new Element("target");

		anchorText.setContent(new CDATA(anchor.getAnchorText()));
		targetURL.setContent(new CDATA(anchor.getTargetURL().toExternalForm()));

		result.addContent(anchorText);
		result.addContent(targetURL);
		return result;
	}

	private Element header2Element(Header header) {
		Element result = new Element("header");
		Element headerText = new Element("text");
		Element size = new Element("size");

		headerText.setContent(new CDATA(header.getContent()));
		size.setContent(new Text(Integer.toString(header.getSize())));

		result.addContent(headerText);
		result.addContent(size);
		return result;
	}

	private Element text2Element(TextContent textContent) {
		Element result = new Element("text");
		result.setContent(new CDATA(textContent.getContent()));
		return result;
	}

	private Element emphasizedText2Element(EmphasizedTextContent emphasizedTextContent) {
		Element result = text2Element(emphasizedTextContent);
		result.setName("emphasizedText");
		Element method = new Element("emphasizedMethod");
		method.setContent(new Text(emphasizedTextContent.getMethod().toString()));
		result.addContent(method);
		return result;
	}
}
