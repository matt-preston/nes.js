package org.perturbed.nesjs.benchmark;

import java.net.*;

import org.perturbed.nesjs.benchmark.client.*;

import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.dev.util.*;
import com.google.gwt.resources.ext.*;
import com.google.gwt.user.rebind.*;

public class BinaryResourceGenerator extends AbstractResourceGenerator implements SupportsGeneratorResultCaching
{
    private static final int MAX_CHUNK_SIZE = 5000;
    @Override
    public String createAssignment(TreeLogger aLogger, ResourceContext aContext, JMethod aMethod) throws UnableToCompleteException
    {
        URL[] _resources = ResourceGeneratorUtil.findResources(aLogger, aContext, aMethod);

        if (_resources.length != 1) 
        {
            aLogger.log(TreeLogger.ERROR, "Exactly one resource must be specified", null);
            throw new UnableToCompleteException();
        }

        URL _resource = _resources[0];
        
        SourceWriter _sw = new StringSourceWriter();
        
        // Write the expression to create the subtype.
        _sw.println("new " + BinaryResource.class.getName() + "() {");
        _sw.indent();

        byte[] _bytes = Util.readURLAsBytes(_resource);
        
        if(_bytes.length > MAX_CHUNK_SIZE)
        {
            writeChunkMethods(_sw, _bytes, aLogger);
        }
        else
        {
            writeGetBytesMethod(_sw, "getBytes", 0, _bytes.length, _bytes);
        }
        
        _sw.println("public String getName() {");
        _sw.indent();
        _sw.println("return \"" + aMethod.getName() + "\";");
        _sw.outdent();
        _sw.println("}");

        _sw.outdent();
        _sw.println("}");

        return _sw.toString();        
    }
    
    private void writeChunkMethods(SourceWriter aSourceWriter, byte[] aBytes, TreeLogger aLogger)
    {
        int _lumps = (int) Math.ceil(aBytes.length / (double) MAX_CHUNK_SIZE);
        
        aLogger.log(Type.DEBUG, "Chunking bytes into " + _lumps + " methods.");
        
        aSourceWriter.println("public int[] getBytes() {");
        aSourceWriter.indent();
        
        aSourceWriter.println("int[] _result = new int[" + aBytes.length + "];");        
        aSourceWriter.println("int _index = 0;");
        
        for(int _index = 0; _index < _lumps; _index++)
        {
            aSourceWriter.println("int[] _chunk%d = getByteArrayChunk%d();", _index, _index);
            aSourceWriter.println("System.arraycopy(_chunk%d, 0, _result, _index, _chunk%d.length);", _index, _index);
            aSourceWriter.println("_index += _chunk%d.length;", _index);
            aSourceWriter.println();
        }
        
        aSourceWriter.println("return _result;");
        
        aSourceWriter.outdent();
        aSourceWriter.println("}");
        
        
        // Write each of the chunk as a separate method
        for(int _index = 0; _index < _lumps; _index++)
        {
            String _method = "getByteArrayChunk" + _index;
            
            int _from = _index * MAX_CHUNK_SIZE;
            int _to = Math.min(aBytes.length, ((_index + 1) * MAX_CHUNK_SIZE));
            
            writeGetBytesMethod(aSourceWriter, _method, _from, _to, aBytes);            
        }
    }
    
    private void writeGetBytesMethod(SourceWriter aSourceWriter, String aName, int aFrom, int aTo, byte[] aBytes)
    {
        aSourceWriter.print("public int[] %s() {\n", aName);
        aSourceWriter.indent();
        
        aSourceWriter.print("return new int[] {");
        
        for(int _index = aFrom; _index < aTo; _index++)
        {
            aSourceWriter.print("%d, ", (aBytes[_index] & 0xFF));
        }
        aSourceWriter.println("};");
        
        aSourceWriter.outdent();
        aSourceWriter.println("}");
    }
}
