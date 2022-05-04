/**
 *  Disclaimer
 *  This project was created by Ryan Davern.
 *  Start Date: 30/03/2016.
 *  
 *  Copyright (C) 2017 Ryan Davern - All Rights Reserved.
 *  You may not use, distribute, monetize or modify this code under the terms of the Copyright Act 1994.
 *  You may use the compiled program, which can be downloaded at https://www.beatplaylist.com/. Any modified versions or versions uploaded to a different website is against TOS (https://www.beatplaylist.com/terms).
 *  
 *  For more information on the Copyright Act 1994, please visit http://www.legislation.govt.nz/act/public/1994/0143/latest/DLM345634.html.
 */

package com.beatplaylist.chromium.adblock;

import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;

public class InterceptRequest implements InterceptUrlRequestCallback {

	@Override
	public Response on(Params params) {
		System.out.println(params.urlRequest().url());
		UrlRequestJob job = params.newUrlRequestJob(UrlRequestJob.Options.newBuilder(HttpStatus.OK).addHttpHeader(HttpHeader.of("Content-Type", "text/javascript")).addHttpHeader(HttpHeader.of("Content-Type", "charset=utf-8")).build());
		// Perform complex calculations and override the responce data in the separate thread.
		new Thread(() -> {
			job.write("const rawPrunePaths=\"{{1}}\",rawNeedlePaths=\"{{2}}\",prunePaths=[\"[].playerResponse.adPlacements\",\"[].playerResponse.playerAds\",\"playerResponse.adPlacements\",\"playerResponse.playerAds\",\"adPlacements\",\"playerAds\"];let needlePaths,log,reLogNeedle;if(0!==prunePaths.length)needlePaths=[];else{let e;log=console.log.bind(console),e=\".?\",reLogNeedle=new RegExp(e)}const findOwner=function(e,n,r=!1){let t=e,s=n;for(;;){if(\"object\"!=typeof t||null===t)return!1;const e=s.indexOf(\".\");if(-1===e){if(!1===r)return t.hasOwnProperty(s);if(\"*\"===s)for(const e in t)!1!==t.hasOwnProperty(e)&&delete t[e];else t.hasOwnProperty(s)&&delete t[s];return!0}const n=s.slice(0,e);if(\"[]\"===n&&Array.isArray(t)||\"*\"===n&&t instanceof Object){const n=s.slice(e+1);let o=!1;for(const e of Object.keys(t))o=findOwner(t[e],n,r)||o;return o}if(!1===t.hasOwnProperty(n))return!1;t=t[n],s=s.slice(e+1)}},mustProcess=function(e){for(const n of needlePaths)if(!1===findOwner(e,n))return!1;return!0},pruner=function(e){if(console.log(JSON.stringify(prunePaths)),void 0!==log){const n=JSON.stringify(e,null,2);return reLogNeedle.test(n)&&log(\"uBO:\",location.hostname,n),e}if(!1===mustProcess(e))return e;for(const n of prunePaths)findOwner(e,n,!0);return e};JSON.parse=new Proxy(JSON.parse,{apply:function(){return pruner(Reflect.apply(...arguments))}}),Response.prototype.json=new Proxy(Response.prototype.json,{apply:function(){return Reflect.apply(...arguments).then(e=>pruner(e))}});".getBytes());
			job.complete();
		}).start();
		return Response.intercept(job);
	}
}