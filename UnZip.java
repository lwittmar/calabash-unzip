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
		//System.out.println(query_doc);
		XdmNode unzip = getChild(query_doc, "c", "http://www.w3.org/ns/xproc-step", "unzip");
		System.out.println(unzip.getAttributeValue(new QName("href")));
		System.out.println(unzip.getAttributeValue(new QName("file")));
		result = "<result>working</result>";
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
