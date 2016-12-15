/*
 * Copyright (c) 2014 The APN-PROXY Project
 *
 * The APN-PROXY Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.kedacom.proxy.netty.forward;

import javax.net.ssl.SSLException;

import com.kedacom.proxy.netty.runtime.SystemContext;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProxyForwardFrontendInitializer<T extends Channel> extends ChannelInitializer<Channel> {

    @Override
    public void initChannel(Channel ch) throws SSLException {
        ch.pipeline().addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));
        
        if("none".equals(SystemContext.getProxyConfig().getMode()) 
        		|| "client".equals(SystemContext.getProxyConfig().getMode())) {
        	//能主动向socks端发起请求
        	ch.pipeline().addLast(new ProxyForwardFrontendHandler(
        			SystemContext.getProxyConfig().getSocksServerConfig().getHost(), 
        			SystemContext.getProxyConfig().getSocksServerConfig().getPort()));
        } else if("server".equals(SystemContext.getProxyConfig().getMode())) {
        	//通知client端发起连接
        	ch.pipeline().addLast(new CmdClientForwardFrontendHandler(
        			SystemContext.getProxyConfig().getSocksServerConfig().getHost(), 
        			SystemContext.getProxyConfig().getSocksServerConfig().getPort()));
        }
    }
}
