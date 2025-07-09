import{w as S}from "./with-props-JNWkkrjn.js";import{a as n,w as C,p as I}from "./chunk-D4RADZKF-DdSvw7tX.js";import{j as e}from "./jsx-runtime-D_zvdyIk.js";import{k as B,j as T,u as E}from "./inquiriesAPI-gt-yzMzF.js";import{u as F}from "./QueryClientProvider--V26__jT.js";import{I as A}from "./inquiry-AmjiLZEt.js";import{I as P}from "./inquiryLoading-DMYYepHY.js";import{S as c}from "./sweetalert2.esm.all-9Qm2jZ7z.js";import{u as v}from "./useTranslation-DnilsLqi.js";import{c as U}from "./createLucideIcon-Cyt6UY_L.js";import{b as z}from "./useInquiryMutation-D96ppYmq.js";import{B as K,F as L}from "./FloatingActionButtons-3zuONfQ9.js";/* empty css                   */import"./axiosInstance-CCaw23o5.js";import"./index-xsH4HHeE.js";import"./index-Druiat5n.js";import"./i18nInstance-CHFDjdcJ.js";import"./useMutation-iBKtBQ4J.js";import"./mutation-CruqbX-z.js";import"./removable-CGh8wCEh.js";/**
 * @license lucide-react v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const O=[["path",{d:"M21.174 6.812a1 1 0 0 0-3.986-3.987L3.842 16.174a2 2 0 0 0-.5.83l-1.321 4.352a.5.5 0 0 0 .623.622l4.353-1.32a2 2 0 0 0 .83-.497z",key:"1a8usu"}]],M=U("pen",O);function R({initialData:i,onSubmit:r,isLoading:u}){const{t:o}=v(),b=F(),[d,m]=n.useState(i.content),[g,p]=n.useState(i.type),[f,w]=n.useState([...i.imgUrls]),[j,a]=n.useState(null),[s,h]=n.useState(!0),y=n.useRef(null),q="https://myapp.peek-pick.click";n.useEffect(()=>{const t=y.current;t&&(t.style.height="auto",t.style.height=`${t.scrollHeight}px`)},[d]);const N=async t=>{try{await B(i.inquiryId,[t]),w(l=>l.filter(x=>x!==t)),await b.invalidateQueries({queryKey:["inquiries"]})}catch(l){console.error("Failed to delete image:",l),await c.fire({title:o("inquiry.deleteImageFail"),icon:"error",confirmButtonText:o("confirm")})}},k=async t=>{if(t.preventDefault(),!s){await c.fire({title:o("inquiry.privacyRequired"),icon:"warning",confirmButtonText:o("confirm")});return}const l={content:d,type:g,imgUrls:f};try{await r(l,j)}catch(x){console.error("Failed to update inquiry:",x),await c.fire({title:o("inquiry.editFail"),icon:"error",confirmButtonText:o("confirm")})}};return u?e.jsx(P,{}):e.jsx("div",{className:"w-full max-w-2xl mx-auto px-4 bg-white rounded-2xl shadow pt-4 pb-6 relative space-y-4",children:e.jsxs("form",{onSubmit:k,className:"space-y-6",children:[e.jsxs("div",{className:"flex items-center space-x-2 mb-4 mt-1.5",children:[e.jsx(M,{className:"text-yellow-500"}),e.jsx("h2",{className:"text-xl font-semibold text-gray-800",children:o("inquiry.editTitle")})]}),e.jsxs("div",{className:"bg-white border rounded-2xl shadow-md px-4 py-6 space-y-4 w-full sm:min-h-[50vh]",children:[e.jsx("select",{value:g,onChange:t=>p(t.target.value),className:"w-full border border-gray-300 p-3 rounded focus:outline-none focus:ring-2 focus:ring-yellow-400",children:A.map(t=>e.jsx("option",{value:t.value,children:o(`inquiry.types.${t.value}`)},t.value))}),e.jsx("textarea",{ref:y,value:d,onChange:t=>m(t.target.value),placeholder:o("inquiry.editPlaceholder"),className:"w-full border border-gray-300 p-3 rounded resize-none focus:outline-none focus:ring-2 focus:ring-yellow-400 overflow-hidden leading-relaxed",rows:1,style:{minHeight:"300px"},required:!0}),f.length>0&&e.jsx("div",{className:"flex flex-wrap gap-3 mb-4",children:f.map(t=>{const l=t.startsWith("http")?t:`${q}${t}`;return e.jsxs("div",{className:"relative w-24 h-24 rounded overflow-hidden border border-gray-300 shadow-sm",children:[e.jsx("img",{src:l,alt:o("inquiry.imageAlt"),className:"w-full h-full object-cover",onError:x=>{x.currentTarget.src=""}}),e.jsx("button",{type:"button",onClick:()=>N(t),className:"absolute top-0 right-0 bg-red-500 text-white rounded-bl px-1.5 py-0.5 text-xs hover:bg-red-600",children:"×"})]},t)})}),e.jsxs("div",{children:[e.jsx("label",{className:"text-sm text-gray-600 mb-1 block",children:o("inquiry.addNewImages")}),e.jsx("input",{type:"file",multiple:!0,onChange:t=>a(t.target.files),className:`block text-sm text-gray-500 file:mr-4 file:py-2 file:px-4\r
                                file:rounded-md file:border-0 file:text-sm file:font-semibold\r
                                file:bg-yellow-500 file:text-white hover:file:bg-yellow-600 cursor-pointer`})]})]}),e.jsxs("div",{className:"mt-4",children:[e.jsxs("label",{className:"inline-flex items-center space-x-2 cursor-pointer select-none text-sm font-normal",children:[e.jsx("input",{type:"checkbox",checked:s,onChange:t=>h(t.target.checked),className:"rounded border-gray-300 text-yellow-500 focus:ring-yellow-400",required:!0}),e.jsx("span",{children:o("inquiry.privacyAgree")})]}),e.jsx("div",{className:"mt-2 w-full p-3 border border-gray-300 rounded bg-gray-100 text-gray-600 text-xs font-sans leading-relaxed whitespace-pre-line select-none",style:{userSelect:"none",pointerEvents:"none",boxShadow:"none"},children:o("inquiry.privacyNotice")})]}),e.jsx("div",{className:"flex justify-end mt-6",children:e.jsx("button",{type:"submit",disabled:!s,className:`w-full px-8 py-3 rounded-full font-semibold text-white transition 
                            ${s?"bg-yellow-500 hover:bg-yellow-600 cursor-pointer":"bg-gray-300 cursor-not-allowed"}`,children:o("inquiry.editSubmit")})})]})})}function $({message:i,onClose:r}){return e.jsxs(e.Fragment,{children:[e.jsx("style",{children:`
              .modal-container {
                background-color: transparent;
              }
              .modal-content {
                width: 90%;
                max-width: 500px;
                padding: 3rem 2rem; /* padding 키움 */
                border-radius: 1rem;
                box-shadow: 0 10px 25px rgba(0,0,0,0.2);
                background: white;
                text-align: center;
                position: relative;
              }
              .modal-message {
                 font-weight: 600;
                 color: #2d3748;
                 line-height: 2; /* 줄 간격 키움 */
                 font-size: 1.5rem; /* 글자 크기 키움 */
                 white-space: pre-line;
               }
              .modal-button {
                margin-top: 2rem; /* 위쪽 여백 키움 */
                width: 100%;
                padding: 1rem 0; /* 세로 패딩 키움 */
                background-color: #2563eb;
                color: white;
                border-radius: 0.75rem; /* 버튼 둥글기 약간 키움 */
                font-weight: 700;
                font-size: 1.25rem; /* 버튼 글자 키움 */
                transition: background-color 0.2s;
                cursor: pointer;
                border: none;
              }
              .modal-button:hover {
                background-color: #1d4ed8;
              }
            
            @media (orientation: portrait) {
              .modal-content {
                max-width: 95vw;
                padding: 2.5rem 1.5rem; /* padding 조금 줄임 */
              }
              .modal-message {
                font-size: 1.2rem;
              }
              .modal-button {
                font-size: 1rem;
                padding: 0.75rem 0;
              }
            }
            `}),e.jsx("div",{className:"fixed inset-0 z-50 flex items-center justify-center modal-container",onClick:r,children:e.jsxs("div",{className:"modal-content",onClick:u=>u.stopPropagation(),children:[e.jsx("p",{className:"modal-message",children:i}),e.jsx("button",{onClick:r,className:"modal-button",children:"OK"})]})})]})}function _(){const{t:i}=v(),{id:r}=C(),u=I(),[o,b]=n.useState(null),[d,m]=n.useState(!0),[g,p]=n.useState(!1),f=z();n.useEffect(()=>{r&&T(+r).then(a=>b(a.data)).catch(a=>{var s;((s=a.response)==null?void 0:s.status)===500&&p(!0)}).finally(()=>{m(!1)})},[r]);const w=async(a,s)=>{var h;if(r){m(!0),c.fire({title:i("inquiry.updating","Updating inquiry..."),allowOutsideClick:!1,allowEscapeKey:!1,didOpen:()=>{c.showLoading()},customClass:{popup:"custom-popup",title:"custom-title",actions:"custom-actions",confirmButton:"custom-confirm-button"}});try{await f.mutateAsync({id:+r,data:a}),s&&s.length>0&&await E(+r,s),await c.fire({title:i("inquiry.updateSuccess","Inquiry updated successfully!"),icon:"success",confirmButtonText:i("confirmOKButtonText","OK"),customClass:{popup:"custom-popup",title:"custom-title",actions:"custom-actions",confirmButton:"custom-confirm-button"}}),u(`/inquiries/${r}`)}catch(y){((h=y.response)==null?void 0:h.status)===500?p(!0):(console.error("Failed to update inquiry"),await c.fire({title:i("inquiry.updateFail","Failed to update inquiry"),icon:"error",confirmButtonText:i("confirmOKButtonText","OK"),customClass:{popup:"custom-popup",title:"custom-title",actions:"custom-actions",confirmButton:"custom-confirm-button"}}))}finally{m(!1)}}},j=()=>{p(!1),u(-1)};return e.jsxs("div",{children:[g&&e.jsx($,{message:i("accessDenied","Access denied."),onClose:j}),e.jsx(K,{}),e.jsx(L,{}),o&&e.jsx(R,{initialData:o,onSubmit:w,isLoading:d}),e.jsx("div",{className:"h-15"})]})}const ue=S(_);export{ue as default};
