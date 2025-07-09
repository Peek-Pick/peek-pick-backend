import{p as d,s as m}from "./chunk-D4RADZKF-DdSvw7tX.js";import{j as a}from "./jsx-runtime-D_zvdyIk.js";import{u as p}from "./useTranslation-DnilsLqi.js";import{c as o}from "./createLucideIcon-Cyt6UY_L.js";/**
 * @license lucide-react v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const h=[["path",{d:"M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8",key:"5wwlr5"}],["path",{d:"M3 10a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z",key:"1d0kgt"}]],x=o("house",h);/**
 * @license lucide-react v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const y=[["path",{d:"M3 7V5a2 2 0 0 1 2-2h2",key:"aa7l1z"}],["path",{d:"M17 3h2a2 2 0 0 1 2 2v2",key:"4qcy5o"}],["path",{d:"M21 17v2a2 2 0 0 1-2 2h-2",key:"6vwrx8"}],["path",{d:"M7 21H5a2 2 0 0 1-2-2v-2",key:"ioqczr"}],["path",{d:"M8 7v10",key:"23sfjj"}],["path",{d:"M12 7v10",key:"jspqdw"}],["path",{d:"M17 7v10",key:"578dap"}]],u=o("scan-barcode",y);/**
 * @license lucide-react v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const v=[["path",{d:"m21 21-4.34-4.34",key:"14j7rj"}],["circle",{cx:"11",cy:"11",r:"8",key:"4ej97u"}]],j=o("search",v);function N(){const{t:e}=p(),c=[{label:e("mainBottomHome"),icon:x,path:"/main"},{label:e("mainBottomBarcode"),icon:u,path:"/barcode/scan"},{label:e("mainBottomSearch"),icon:j,path:"/products/search"}],n=d(),{pathname:r}=m();return a.jsxs(a.Fragment,{children:[a.jsx("div",{className:"h-20"}),a.jsx("nav",{className:"fixed bottom-0 left-0 w-full h-15 bg-transparent backdrop-blur-md shadow-2xl flex justify-around items-center z-50",style:{boxShadow:"0 -4px 12px rgba(0, 0, 0, 0.1)"},children:c.map(({label:i,icon:l,path:t})=>{const s=r===t;return a.jsxs("button",{onClick:()=>n(t),className:"flex flex-col items-center justify-center gap-[2px] text-[10px]",children:[a.jsx(l,{className:`w-6 h-6 ${s?"text-yellow-500":"text-gray-500"}`}),a.jsx("span",{className:s?"text-yellow-500":"text-gray-500",children:i})]},t)})})]})}export{N as B};
