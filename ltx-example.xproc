<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:ltx="http://le-tex.de/tools/unzip"
                name="pipeline"
                version="1.0">

   <p:import href="ltx-lib.xpl"/>

   <p:output port="result" primary="true"/>

<ltx:unzip-files>
    <p:input port="source">
	<p:inline>
	  <c:unzip href="hello-world.zip" file="pics/world.jpg"/>
	</p:inline>
    </p:input>
   </ltx:unzip-files>
</p:declare-step>
