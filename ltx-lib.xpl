<p:library xmlns:p="http://www.w3.org/ns/xproc"
           xmlns:ltx="http://le-tex.de/tools/unzip"
           xmlns:pkg="http://expath.org/ns/pkg"
           pkg:import-uri="http://le-tex.de/tools/unzip.xpl"
           version="1.0">

   <p:declare-step type="ltx:unzip-files">
      <p:input  port="source" primary="true"/>
      <p:output port="result" primary="true"/>
   </p:declare-step>

</p:library>
