/**
 * Unzip extension for non-XML files
 *
 * @author Lars Wittmar -- le-tex publishing services GmbH
 * @date   2012-03-08
 **/

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.library.DefaultStep;
import com.xmlcalabash.runtime.XAtomicStep;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Axis;
import java.io.*;
import java.io.StringReader;
import java.io.File;
import java.util.zip.*;

public class UnZip
    extends DefaultStep
    {
	public UnZip(XProcRuntime runtime, XAtomicStep step)
	{
	    super(runtime,step);
	}

	@Override
	    public void setInput(String port, ReadablePipe pipe)
	    {
		mySource = pipe;
	    }

	@Override
	    public void setOutput(String port, WritablePipe pipe)
	    {
		myResult = pipe;
	    }

	@Override
	    public void reset()
	    {
		mySource.resetReader();
		myResult.resetWriter();
	    }
	@Override
	    public void run()
	    throws SaxonApiException
	{
	    super.run();

	    String result;
	    try {
		XdmNode query_doc = mySource.read();
		XdmNode unzip = getChild(query_doc, "c", "http://www.w3.org/ns/xproc-step", "unzip");

		//getting attributes of c:unzip
		//variable names equal attribute names
		String href = "";
		String file = "";
		String dest = "";

		if(unzip.getAttributeValue(new QName("href"))!=null) 
		    href = unzip.getAttributeValue(new QName("href"));
		if(unzip.getAttributeValue(new QName("file"))!=null) 
		    file = unzip.getAttributeValue(new QName("file"));
		if(unzip.getAttributeValue(new QName("dest"))!=null) 
		    dest = unzip.getAttributeValue(new QName("dest"));
		


		if(dest!="") {
		    File folder = new File(dest);
		    if(! folder.exists()) folder.mkdir(); 
		}

		final int BUFFER = 2048;
		BufferedOutputStream bos = null;
		FileInputStream fis = new FileInputStream(href);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;

		result = "<files>";
		//without given fileNode extract everything
		if(file.equals("")) {
		    System.out.println("HIER");
		    while((entry = zis.getNextEntry()) != null) {
			System.out.println("Extracting: " +entry);
			int count;
			byte data[] = new byte[BUFFER];

			if(entry.isDirectory()) {
			    File dir = new File (dest + "/" + entry);
			    if (!dir.exists()) dir.mkdir();
			} else {
			    FileOutputStream fos = new FileOutputStream(dest + "/" + entry.getName());
			    result += "<file>" + entry.getName() + "</file>";
			    bos = new BufferedOutputStream(fos, BUFFER);
			    while ((count = zis.read(data, 0, BUFFER)) != -1) {
				bos.write(data, 0, count);
			    }
			    bos.flush();
			    bos.close();
			}
		    }
		} else {
		    while((entry = zis.getNextEntry()) != null) {
			if(file.equals(entry.getName())) {
			    System.out.println("Extracting: " +entry);
			    createDir(entry.getName(),dest);
			    int count;
			    byte data[] = new byte[BUFFER];

			    FileOutputStream fos = new FileOutputStream(dest + "/" + entry.getName());
			    result += "<file>" + entry.getName() + "</file>";
			    bos = new BufferedOutputStream(fos, BUFFER);
			    while ((count = zis.read(data, 0, BUFFER)) != -1) {
				bos.write(data, 0, count);
			    }
			    bos.flush();
			    bos.close();
			}
		    }
		}
		zis.close();
		result += "</files>";
	    } catch(Exception e) {
		result = "<error>FEHLER</error>";
		e.printStackTrace();
	    }
	    DocumentBuilder builder = runtime.getProcessor().newDocumentBuilder();
	    Source src = new StreamSource(new StringReader(result));
	    XdmNode doc = builder.build(src);

	    myResult.write(doc);
	}


	/**
	 * Helper to create directory structure
	 */
	public static void createDir(String dirname, String dest) {
	    String[] path = dirname.split("/");
	    for(int i=0; i<=(path.length-2);i++) {
		File dir = new File (dest + "/" + path[i]);
		if (!dir.exists()) dir.mkdir();
	    }
	}

    /**
     * Helper method to get the first child of an element having a given name.
     * If there is no child with the given name it returns null.
     */
    public static XdmNode getChild(XdmNode parent, String prefix, String uri, String childName) {
	XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, new QName(prefix, uri, childName));
	if (iter.hasNext()) {
	    return (XdmNode)iter.next();
	} else {
	    return null;
	}
    }

	private ReadablePipe mySource = null;
	private WritablePipe myResult = null;
    }
