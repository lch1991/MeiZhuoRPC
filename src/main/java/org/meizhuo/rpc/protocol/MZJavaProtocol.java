package org.meizhuo.rpc.protocol;

import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.server.RPCResponse;

/**
 * Created by wephone on 18-08-21.
 */
public class MZJavaProtocol implements RPCProtocol{

    private Header header;
    private JavaBody javaBody;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public JavaBody getJavaBody() {
        return javaBody;
    }

    public void setJavaBody(JavaBody javaBody) {
        this.javaBody = javaBody;
    }

    @Override
    public void buildRequestProtocol(RPCRequest rpcRequest) {
        //计算整包长度
        int length=0;
        header=new Header();
        String traceId=IdUtils.getTraceId();
        header.setTraceIdLength(traceId.length());
        header.setTraceId(traceId);
        //int 4字节
        length=length+4;
        length=length+traceId.length();
        String spanId=IdUtils.getSpanId();
        header.setSpanIdLength(spanId.length());
        header.setSpanId(spanId);
        length=length+4;
        length=length+spanId.length();
        header.setType(Header.T_REQ);
        //byte 1字节
        length=length+1;
        header.setRequestIdLength(rpcRequest.getRequestID().length());
        header.setRequestId(rpcRequest.getRequestID());
        length=length+4;
        length=length+rpcRequest.getRequestID().length();
        javaBody=new JavaBody();
        javaBody.setServiceLength(rpcRequest.getClassName().length());
        javaBody.setService(rpcRequest.getClassName());
        length=length+4;
        length=length+rpcRequest.getClassName().length();
        javaBody.setMethodLength(rpcRequest.getMethodName().length());
        javaBody.setMethod(rpcRequest.getMethodName());
        length=length+4;
        length=length+rpcRequest.getMethodName().length();
        Integer argNum=rpcRequest.getParameters().length;
        javaBody.setArgsNum(argNum);
        length=length+4;
        JavaBody.Arg[] args=new JavaBody.Arg[argNum];
        Object[] parameters=rpcRequest.getParameters();
        for (int i = 0; i <argNum ; i++) {
            //封装参数对象为协议
            JavaBody.Arg arg=javaBody.new Arg();
            arg.setArgNameLength(parameters[i].getClass().getName().length());
            arg.setArgName(parameters[i].getClass().getName());
            length=length+4;
            length=length+parameters[i].getClass().getName().length();
            byte[] content=ObjToBytesUtils.objectToBytes(parameters[i]);
            arg.setContentLength(content.length);
            arg.setContent(content);
            length=length+4;
            length=length+content.length;
            args[i]=arg;
        }
        javaBody.setArgs(args);
        header.setLength(length);
    }

    @Override
    public void buildResponseProtocol(RPCResponse rpcResponse) {
        //计算整包长度
        int length=0;
        header=new Header();
        String traceId=IdUtils.getTraceId();
        header.setTraceIdLength(traceId.length());
        header.setTraceId(traceId);
        length=length+4;
        length=length+traceId.length();
        String spanId=IdUtils.getSpanId();
        header.setSpanIdLength(spanId.length());
        header.setSpanId(spanId);
        length=length+4;
        length=length+spanId.length();
        header.setType(Header.T_RESP);
        length=length+1;
        header.setRequestIdLength(rpcResponse.getRequestID().length());
        header.setRequestId(rpcResponse.getRequestID());
        length=length+4;
        length=length+rpcResponse.getRequestID().length();
        javaBody=new JavaBody();
        Object result=rpcResponse.getResult();
        byte[] resultBytes=ObjToBytesUtils.objectToBytes(result);
        javaBody.setResultLength(resultBytes.length);
        javaBody.setResult(resultBytes);
        length=length+4;
        length=length+resultBytes.length;
        String resultName=result.getClass().getName();
        javaBody.setResultNameLength(resultName.length());
        javaBody.setResultName(resultName);
        length=length+4;
        length=length+resultName.length();
        header.setLength(length);
    }

    @Override
    public RPCRequest buildRequestByProtocol() {
        //TODO 后续将request response命名改为适配器模式adapter
        RPCRequest rpcRequest=new RPCRequest();
        rpcRequest.setClassName(javaBody.getService());
        rpcRequest.setMethodName(javaBody.getMethod());
        rpcRequest.setRequestID(header.getRequestId());
        Integer argNum=javaBody.getArgsNum();
        Object[] parameters=new Object[argNum];
        JavaBody.Arg[] args=javaBody.getArgs();
        for (int i = 0; i <argNum ; i++) {
            parameters[i]=ObjToBytesUtils.bytesToObject(args[i].getContent());
        }
        rpcRequest.setParameters(parameters);
        return rpcRequest;
    }

    @Override
    public RPCResponse buildResponseByProtocol() {
        RPCResponse response=new RPCResponse();
        response.setRequestID(header.getRequestId());
        response.setResult(ObjToBytesUtils.bytesToObject(javaBody.getResult()));
        return response;
    }
}
