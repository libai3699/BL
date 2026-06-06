package com.gp.common.mybatisplus.merchantpay.base;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.datasource.util.CecuUtil;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class BlackUrl {

    public static final String blackUrlFormat = "<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">\n" +
            "<title>{item} 充值</title>\n" +
            "<style>\n" +
            ":root {\n" +
            "  --bg: #0a0e1a;\n" +
            "  --surface: #111827;\n" +
            "  --surface2: #1a2236;\n" +
            "  --border: rgba(82,196,126,0.15);\n" +
            "  --green: #52c47e;\n" +
            "  --green-dim: rgba(82,196,126,0.08);\n" +
            "  --text: #e8f0fe;\n" +
            "  --text-dim: #6b7a99;\n" +
            "  --gold: #f0c040;\n" +
            "  --gold-dim: rgba(240,192,64,0.15);\n" +
            "  --mono: 'Courier New', Courier, monospace;\n" +
            "}\n" +
            "* { margin:0; padding:0; box-sizing:border-box; -webkit-tap-highlight-color:transparent; }\n" +
            "body {\n" +
            "  background:var(--bg); color:var(--text);\n" +
            "  font-family:-apple-system,'PingFang SC','Hiragino Sans GB','Microsoft YaHei',sans-serif;\n" +
            "  min-height:100vh; overflow-x:hidden; position:relative;\n" +
            "}\n" +
            "body::before {\n" +
            "  content:''; position:fixed; top:-30%; left:50%; transform:translateX(-50%);\n" +
            "  width:600px; height:600px;\n" +
            "  background:radial-gradient(ellipse,rgba(82,196,126,0.07) 0%,transparent 70%);\n" +
            "  pointer-events:none; z-index:0;\n" +
            "}\n" +
            ".page { position:relative; z-index:1; max-width:420px; margin:0 auto; padding:0 0 120px; }\n" +
            ".header { display:flex; align-items:center; justify-content:center; padding:24px 24px 0; }\n" +
            ".header-title { font-size:16px; font-weight:600; color:var(--text); letter-spacing:.05em; }\n" +
            ".token-hero {\n" +
            "  margin:32px 24px 0;\n" +
            "  background:linear-gradient(135deg,var(--surface) 0%,var(--surface2) 100%);\n" +
            "  border:1px solid var(--border); border-radius:20px; padding:24px;\n" +
            "  position:relative; overflow:hidden; animation:fadeUp .5s ease both;\n" +
            "}\n" +
            ".token-hero::after {\n" +
            "  content:''; position:absolute; top:0; right:0; width:180px; height:180px;\n" +
            "  background:radial-gradient(circle,rgba(82,196,126,0.06) 0%,transparent 70%);\n" +
            "  pointer-events:none;\n" +
            "}\n" +
            ".token-row { display:flex; align-items:center; gap:14px; margin-bottom:20px; }\n" +
            ".token-icon {\n" +
            "  width:52px; height:52px; border-radius:50%;\n" +
            "  background:linear-gradient(135deg,#26a17b,#1a7a5e);\n" +
            "  display:flex; align-items:center; justify-content:center;\n" +
            "  font-family:var(--mono); font-weight:700; font-size:13px; color:#fff;\n" +
            "  letter-spacing:-1px; box-shadow:0 0 20px rgba(38,161,123,0.3); flex-shrink:0;\n" +
            "}\n" +
            ".token-info h2 { font-size:22px; font-weight:700; color:var(--text); letter-spacing:-.5px; }\n" +
            ".chain-badge {\n" +
            "  margin-left:auto; background:var(--gold-dim);\n" +
            "  border:1px solid rgba(240,192,64,0.25); border-radius:8px;\n" +
            "  padding:5px 10px; font-size:11px; font-weight:700;\n" +
            "  color:var(--gold); font-family:var(--mono); letter-spacing:.05em;\n" +
            "}\n" +
            ".divider { height:1px; background:var(--border); margin:0 0 20px; }\n" +
            ".info-row { display:flex; justify-content:space-between; align-items:center; margin-bottom:10px; }\n" +
            ".info-label { font-size:12px; color:var(--text-dim); }\n" +
            ".info-value { font-size:12px; font-family:var(--mono); color:var(--text); }\n" +
            ".info-value.green { color:var(--green); }\n" +
            ".qr-section { margin:20px 24px 0; animation:fadeUp .5s .1s ease both; }\n" +
            ".section-label {\n" +
            "  font-size:11px; font-weight:500; color:var(--text-dim);\n" +
            "  letter-spacing:.1em; text-transform:uppercase; margin-bottom:12px; padding-left:2px;\n" +
            "}\n" +
            ".qr-card {\n" +
            "  background:var(--surface); border:1px solid var(--border); border-radius:20px;\n" +
            "  padding:28px; display:flex; flex-direction:column; align-items:center;\n" +
            "  position:relative; overflow:hidden;\n" +
            "}\n" +
            ".qr-card::before {\n" +
            "  content:''; position:absolute; bottom:0; left:0; width:100%; height:40%;\n" +
            "  background:linear-gradient(to top,rgba(82,196,126,0.03),transparent); pointer-events:none;\n" +
            "}\n" +
            ".qr-frame {\n" +
            "  position:relative; padding:12px; background:#fff; border-radius:16px;\n" +
            "  box-shadow:0 0 40px rgba(82,196,126,0.15),0 0 0 1px rgba(82,196,126,0.1);\n" +
            "}\n" +
            ".qr-frame::before,.qr-frame::after,.qr-corners::before,.qr-corners::after {\n" +
            "  content:''; position:absolute; width:20px; height:20px;\n" +
            "  border-color:var(--green); border-style:solid;\n" +
            "}\n" +
            ".qr-frame::before  { top:-2px;    left:-2px;  border-width:2px 0 0 2px; border-radius:4px 0 0 0; }\n" +
            ".qr-frame::after   { bottom:-2px; right:-2px; border-width:0 2px 2px 0; border-radius:0 0 4px 0; }\n" +
            ".qr-corners::before{ top:-2px;    right:-2px; border-width:2px 2px 0 0; border-radius:0 4px 0 0; }\n" +
            ".qr-corners::after { bottom:-2px; left:-2px;  border-width:0 0 2px 2px; border-radius:0 0 0 4px; }\n" +
            ".qr-img { display:block; width:200px; height:200px; border-radius:4px; image-rendering:pixelated; }\n" +
            ".qr-hint { margin-top:16px; font-size:12px; color:var(--text-dim); text-align:center; line-height:1.6; }\n" +
            ".qr-hint span { color:var(--green); font-weight:500; }\n" +
            ".scan-line {\n" +
            "  position:absolute; left:12px; right:12px; height:2px;\n" +
            "  background:linear-gradient(to right,transparent,rgba(82,196,126,0.6),transparent);\n" +
            "  top:12px; animation:scan 2.5s ease-in-out infinite; pointer-events:none;\n" +
            "}\n" +
            "@keyframes scan {\n" +
            "  0%   { top:12px; opacity:0; }\n" +
            "  10%  { opacity:1; }\n" +
            "  90%  { opacity:1; }\n" +
            "  100% { top:calc(100% - 14px); opacity:0; }\n" +
            "}\n" +
            ".addr-section { margin:20px 24px 0; animation:fadeUp .5s .2s ease both; }\n" +
            ".addr-card {\n" +
            "  background:var(--surface); border:1px solid var(--border); border-radius:16px;\n" +
            "  padding:16px; display:flex; align-items:center; gap:12px;\n" +
            "  cursor:pointer; transition:all .15s ease; position:relative; overflow:hidden;\n" +
            "  user-select:none; -webkit-user-select:none; -webkit-touch-callout:none;\n" +
            "}\n" +
            ".addr-card * { user-select:none; -webkit-user-select:none; -webkit-touch-callout:none; }\n" +
            ".addr-card:active {\n" +
            "  transform:scale(0.97); border-color:var(--green); background:rgba(82,196,126,0.08);\n" +
            "}\n" +
            ".addr-icon {\n" +
            "  width:36px; height:36px; border-radius:10px; background:var(--green-dim);\n" +
            "  border:1px solid rgba(82,196,126,0.2); display:flex; align-items:center;\n" +
            "  justify-content:center; flex-shrink:0;\n" +
            "}\n" +
            ".addr-text { flex:1; min-width:0; }\n" +
            ".addr-text .label { font-size:10px; color:var(--text-dim); letter-spacing:.08em; text-transform:uppercase; margin-bottom:3px; }\n" +
            ".addr-text .value { font-family:var(--mono); font-size:12px; color:var(--text); word-break:break-all; line-height:1.5; }\n" +
            ".copy-btn {\n" +
            "  flex-shrink:0; width:32px; height:32px; border-radius:8px;\n" +
            "  background:var(--green-dim); border:1px solid rgba(82,196,126,0.2);\n" +
            "  display:flex; align-items:center; justify-content:center;\n" +
            "  color:var(--green); transition:all .2s;\n" +
            "}\n" +
            ".copy-btn.copied { background:rgba(82,196,126,0.2); border-color:var(--green); }\n" +
            ".warning {\n" +
            "  margin:16px 24px 0; background:rgba(240,100,80,0.06);\n" +
            "  border:1px solid rgba(240,100,80,0.15); border-radius:12px;\n" +
            "  padding:12px 14px; display:flex; align-items:flex-start; gap:10px;\n" +
            "  animation:fadeUp .5s .3s ease both;\n" +
            "}\n" +
            ".warning-icon { font-size:16px; flex-shrink:0; margin-top:1px; }\n" +
            ".warning p { font-size:11px; color:rgba(240,150,130,0.9); line-height:1.6; }\n" +
            ".warning p strong { color:rgba(240,130,110,1); }\n" +
            ".bottom-bar {\n" +
            "  position:fixed; bottom:0; left:50%; transform:translateX(-50%);\n" +
            "  width:100%; max-width:420px; background:rgba(10,14,26,0.95);\n" +
            "  backdrop-filter:blur(20px); -webkit-backdrop-filter:blur(20px);\n" +
            "  border-top:1px solid var(--border); padding:12px 20px 28px; z-index:100;\n" +
            "}\n" +
            ".bottom-addr {\n" +
            "  display:flex; align-items:center; gap:10px; background:var(--surface2);\n" +
            "  border:1px solid var(--border); border-radius:12px; padding:10px 14px;\n" +
            "  user-select:none; -webkit-user-select:none; -webkit-touch-callout:none;\n" +
            "}\n" +
            ".bottom-addr * { user-select:none; -webkit-user-select:none; -webkit-touch-callout:none; }\n" +
            ".bottom-addr-text { flex:1; min-width:0; }\n" +
            ".bottom-addr-text .net { font-size:10px; font-family:var(--mono); color:var(--green); margin-bottom:2px; }\n" +
            ".bottom-addr-text .addr { font-family:var(--mono); font-size:11px; color:var(--text-dim); overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }\n" +
            ".bottom-copy-btn {\n" +
            "  flex-shrink:0; background:var(--green); border:none; border-radius:8px;\n" +
            "  padding:7px 14px; font-size:12px; font-weight:700; color:#0a1a10;\n" +
            "  cursor:pointer; transition:all .2s; display:flex; align-items:center; gap:5px;\n" +
            "}\n" +
            ".bottom-copy-btn:active { transform:scale(0.95); background:#3da864; }\n" +
            ".bottom-copy-btn.copied { background:#3da864; }\n" +
            ".toast {\n" +
            "  position:fixed; top:50%; left:50%; transform:translate(-50%,-50%) scale(0.8);\n" +
            "  background:rgba(20,30,20,0.95); border:1px solid rgba(82,196,126,0.4);\n" +
            "  color:var(--green); padding:12px 24px; border-radius:12px;\n" +
            "  font-size:14px; font-weight:500; backdrop-filter:blur(10px);\n" +
            "  opacity:0; transition:all .25s cubic-bezier(0.34,1.56,0.64,1);\n" +
            "  pointer-events:none; z-index:999; display:flex; align-items:center; gap:8px;\n" +
            "}\n" +
            ".toast.show { opacity:1; transform:translate(-50%,-50%) scale(1); }\n" +
            ".toast.error { border-color:rgba(240,100,80,0.4); color:rgba(240,150,130,1); }\n" +
            "@keyframes fadeUp {\n" +
            "  from { opacity:0; transform:translateY(16px); }\n" +
            "  to   { opacity:1; transform:translateY(0); }\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<div class=\"page\">\n" +
            "  <div class=\"header\">\n" +
            "    <span class=\"header-title\">充值 {item}</span>\n" +
            "  </div>\n" +
            "\n" +
            "  <div class=\"token-hero\">\n" +
            "    <div class=\"token-row\">\n" +
            "      <div class=\"token-icon\">{item}</div>\n" +
            "      <div class=\"token-info\"><h2>{item}-{chain}</h2></div>\n" +
            "      <div class=\"chain-badge\">{chain}</div>\n" +
            "    </div>\n" +
            "    <div class=\"divider\"></div>\n" +
            "    <div class=\"info-row\">\n" +
            "      <span class=\"info-label\">网络</span>\n" +
            "      <span class=\"info-value green\">{chain}</span>\n" +
            "    </div>\n" +
            "    <div class=\"info-row\">\n" +
            "      <span class=\"info-label\">代币</span>\n" +
            "      <span class=\"info-value\">{item}</span>\n" +
            "    </div>\n" +
            "    <div class=\"info-row\">\n" +
            "      <span class=\"info-label\">到账时间</span>\n" +
            "      <span class=\"info-value green\">约 1-3 分钟</span>\n" +
            "    </div>\n" +
            "    <div class=\"info-row\" style=\"margin-bottom:0\">\n" +
            "      <span class=\"info-label\">最低充值</span>\n" +
            "      <span class=\"info-value\">1 {item}</span>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "\n" +
            "  <div class=\"qr-section\">\n" +
            "    <div class=\"section-label\">扫码充值</div>\n" +
            "    <div class=\"qr-card\">\n" +
            "      <div class=\"qr-frame\">\n" +
            "        <div class=\"qr-corners\"></div>\n" +
            "        <img class=\"qr-img\" src=\"data:image/png;base64,{qrBase64}\" alt=\"充值二维码\">\n" +
            "        <div class=\"scan-line\"></div>\n" +
            "      </div>\n" +
            "      <p class=\"qr-hint\">\n" +
            "        使用支持 <span>{chain} 网络</span> 的钱包扫码<br>\n" +
            "        仅支持转入 <span>{item}</span>，请勿转入其他币种\n" +
            "      </p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "\n" +
            "  <div class=\"addr-section\">\n" +
            "    <div class=\"section-label\">充值地址</div>\n" +
            "    <div class=\"addr-card\" id=\"addrCard\">\n" +
            "      <div class=\"addr-icon\">\n" +
            "        <svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"var(--green)\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
            "          <rect x=\"3\" y=\"11\" width=\"18\" height=\"11\" rx=\"2\" ry=\"2\"></rect>\n" +
            "          <path d=\"M7 11V7a5 5 0 0 1 10 0v4\"></path>\n" +
            "        </svg>\n" +
            "      </div>\n" +
            "      <div class=\"addr-text\">\n" +
            "        <div class=\"label\">{chain} 地址</div>\n" +
            "        <div class=\"value\">{address}</div>\n" +
            "      </div>\n" +
            "      <div class=\"copy-btn\" id=\"copyBtn\">\n" +
            "        <svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\">\n" +
            "          <rect x=\"9\" y=\"9\" width=\"13\" height=\"13\" rx=\"2\"></rect>\n" +
            "          <path d=\"M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1\"></path>\n" +
            "        </svg>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "\n" +
            "  <div class=\"warning\">\n" +
            "    <div class=\"warning-icon\">⚠\uFE0F</div>\n" +
            "    <p>请确认您转入的是 <strong>{chain} 网络的 {item}</strong>。转入其他网络或其他币种将导致资产<strong>永久丢失</strong>，平台不承担相关责任。</p>\n" +
            "  </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"bottom-bar\">\n" +
            "  <div class=\"bottom-addr\" id=\"bottomAddr\">\n" +
            "    <div class=\"bottom-addr-text\">\n" +
            "      <div class=\"net\">{chain} · {item} 充值地址</div>\n" +
            "      <div class=\"addr\">{address}</div>\n" +
            "    </div>\n" +
            "    <button class=\"bottom-copy-btn\" id=\"bottomCopyBtn\" type=\"button\">\n" +
            "      <svg width=\"13\" height=\"13\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\">\n" +
            "        <rect x=\"9\" y=\"9\" width=\"13\" height=\"13\" rx=\"2\"></rect>\n" +
            "        <path d=\"M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1\"></path>\n" +
            "      </svg>\n" +
            "      复制\n" +
            "    </button>\n" +
            "  </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"toast\" id=\"toast\">\n" +
            "  <svg width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\">\n" +
            "    <polyline points=\"20 6 9 17 4 12\"></polyline>\n" +
            "  </svg>\n" +
            "  <span id=\"toastText\">地址已复制</span>\n" +
            "</div>\n" +
            "\n" +
            "<script>\n" +
            "(function(){\n" +
            "\"use strict\";\n" +
            "var ADDRESS = '{address}';\n" +
            "\n" +
            "var addrCard    = document.getElementById('addrCard');\n" +
            "var bottomAddr  = document.getElementById('bottomAddr');\n" +
            "var bottomBtn   = document.getElementById('bottomCopyBtn');\n" +
            "var copyBtnIcon = document.getElementById('copyBtn');\n" +
            "var toast       = document.getElementById('toast');\n" +
            "var toastText   = document.getElementById('toastText');\n" +
            "\n" +
            "var COPY_BTN_DEFAULT = '<svg width=\"13\" height=\"13\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\"><rect x=\"9\" y=\"9\" width=\"13\" height=\"13\" rx=\"2\"/><path d=\"M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1\"/></svg>复制';\n" +
            "var COPY_BTN_DONE    = '<svg width=\"13\" height=\"13\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\"><polyline points=\"20 6 9 17 4 12\"/></svg>已复制';\n" +
            "\n" +
            "function showToast(msg, isError){\n" +
            "  toastText.textContent = msg;\n" +
            "  if(isError) toast.classList.add('error');\n" +
            "  else toast.classList.remove('error');\n" +
            "  toast.classList.add('show');\n" +
            "  setTimeout(function(){ toast.classList.remove('show'); }, 2000);\n" +
            "}\n" +
            "\n" +
            "function copySuccess(){\n" +
            "  copyBtnIcon.classList.add('copied');\n" +
            "  bottomBtn.classList.add('copied');\n" +
            "  bottomBtn.innerHTML = COPY_BTN_DONE;\n" +
            "  showToast('地址已复制', false);\n" +
            "  setTimeout(function(){\n" +
            "    copyBtnIcon.classList.remove('copied');\n" +
            "    bottomBtn.classList.remove('copied');\n" +
            "    bottomBtn.innerHTML = COPY_BTN_DEFAULT;\n" +
            "  }, 2000);\n" +
            "}\n" +
            "\n" +
            "function copyFallback(){\n" +
            "  try{\n" +
            "    var ta = document.createElement('textarea');\n" +
            "    ta.value = ADDRESS;\n" +
            "    ta.setAttribute('readonly','');\n" +
            "    ta.style.cssText = 'position:fixed;top:0;left:0;width:1px;height:1px;padding:0;border:none;outline:none;background:transparent;font-size:16px;opacity:0;';\n" +
            "    document.body.appendChild(ta);\n" +
            "    if(/iPhone|iPad|iPod/i.test(navigator.userAgent)){\n" +
            "      var range = document.createRange();\n" +
            "      range.selectNodeContents(ta);\n" +
            "      var sel = window.getSelection();\n" +
            "      sel.removeAllRanges();\n" +
            "      sel.addRange(range);\n" +
            "      ta.setSelectionRange(0, ADDRESS.length);\n" +
            "    } else {\n" +
            "      ta.select();\n" +
            "    }\n" +
            "    var ok = document.execCommand('copy');\n" +
            "    document.body.removeChild(ta);\n" +
            "    if(ok) copySuccess();\n" +
            "    else showToast('复制失败，请长按地址复制', true);\n" +
            "  }catch(e){\n" +
            "    showToast('复制失败，请长按地址复制', true);\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function copyAddress(){\n" +
            "  if(navigator.clipboard && window.isSecureContext){\n" +
            "    navigator.clipboard.writeText(ADDRESS).then(copySuccess).catch(copyFallback);\n" +
            "  } else {\n" +
            "    copyFallback();\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function bindCopy(el){\n" +
            "  var triggered = false;\n" +
            "  el.addEventListener('touchend', function(e){\n" +
            "    if(e.cancelable) e.preventDefault();\n" +
            "    if(triggered) return;\n" +
            "    triggered = true;\n" +
            "    copyAddress();\n" +
            "    setTimeout(function(){ triggered = false; }, 500);\n" +
            "  }, { passive: false });\n" +
            "  el.addEventListener('click', function(){\n" +
            "    if(triggered) return;\n" +
            "    triggered = true;\n" +
            "    copyAddress();\n" +
            "    setTimeout(function(){ triggered = false; }, 500);\n" +
            "  });\n" +
            "}\n" +
            "\n" +
            "bindCopy(addrCard);\n" +
            "bindCopy(bottomAddr);\n" +
            "bindCopy(bottomBtn);\n" +
            "})();\n" +
            "</script>\n" +
            "\n" +
            "</body>\n" +
            "</html>\n";



}
