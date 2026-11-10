const {spawn}=require('node:child_process')
const assert=require('node:assert/strict')
const {chromium}=require('playwright')
const origin='http://127.0.0.1:3240'
const development=process.argv.includes('--dev')
;(async()=>{
 const server=spawn(process.execPath,['node_modules/vite/bin/vite.js',...(development?[]:['preview']),'--host','127.0.0.1','--port','3240','--strictPort'],{stdio:'ignore',env:{...process.env,VITE_API_URL:'/api'}})
 let browser
 try{
  let ready=false;for(let i=0;i<100;i++){try{await fetch(origin);ready=true;break}catch{await new Promise(r=>setTimeout(r,100))}}assert.ok(ready,development?'development server starts':'production preview starts')
  browser=await chromium.launch({headless:true,...(process.env.CHROME_PATH?{executablePath:process.env.CHROME_PATH}:{})})
  const page=await browser.newPage({viewport:{width:1280,height:900}})
  const errors=[],requests=[];page.on('pageerror',e=>errors.push(e.message))
  const user={id:1,name:'Fixture User',email:'fixture@example.invalid'}
  let rejectHistory=false
  await page.route('**/*',async route=>{
   const req=route.request(),url=new URL(req.url())
   if(url.origin!==origin)return route.abort()
   if(!url.pathname.startsWith('/api/'))return route.continue()
   requests.push({path:url.pathname,method:req.method(),auth:req.headers().authorization,data:req.postData()?req.postDataJSON():null})
   let data
   if(url.pathname==='/api/auth/login')data={token:'synthetic-test-token',user}
   else if(url.pathname==='/api/auth/me')data=user
   else if(url.pathname==='/api/questions/history'&&rejectHistory)return route.fulfill({status:401,contentType:'application/json',body:JSON.stringify({message:'Expired fixture'})})
   else if(['/api/questions/history','/api/favorites'].includes(url.pathname))data={content:[],totalPages:0,totalElements:0}
   else throw new Error('Unexpected API request: '+url.pathname)
   return route.fulfill({contentType:'application/json',body:JSON.stringify(data)})
  })
  await page.goto(origin);await page.getByRole('heading',{name:'You told everyone to practice. Now give them a secure place to do it.'}).waitFor()
  if(process.env.SCREENSHOT_DIR)await page.screenshot({path:process.env.SCREENSHOT_DIR+'/orator-home.png',fullPage:true})
  await page.setViewportSize({width:390,height:844});if(process.env.SCREENSHOT_DIR)await page.screenshot({path:process.env.SCREENSHOT_DIR+'/orator-mobile.png',fullPage:true})
  await page.setViewportSize({width:1280,height:900});await page.goto(origin+'/dashboard');await page.waitForURL('**/login');await page.getByRole('heading',{name:'Welcome back'}).waitFor()
  await page.getByRole('link',{name:'Create one'}).click();await page.getByRole('heading',{name:'Create an account'}).waitFor()
  await page.goto(origin+'/login');await page.locator('input[type=email]').fill(user.email);await page.locator('input[type=password]').fill('synthetic-not-a-real-password');await page.getByRole('button',{name:'Sign In',exact:true}).click();await page.waitForURL('**/dashboard');await page.getByRole('heading',{name:'Ready to practice, fixture?'}).waitFor()
  assert.ok(requests.some(r=>r.path==='/api/auth/login'&&r.method==='POST'&&r.data.email===user.email),'Axios submits login JSON')
  await page.getByRole('link',{name:'History',exact:true}).click();await page.getByRole('heading',{name:'Session History'}).waitFor()
  assert.ok(requests.some(r=>r.path==='/api/questions/history'&&r.auth==='Bearer synthetic-test-token'),'Axios attaches token')
  await page.getByRole('link',{name:'Favorites',exact:true}).click();await page.getByRole('heading',{name:'Favorites',exact:true}).waitFor()
  await page.getByRole('link',{name:'Generate',exact:true}).click();await page.getByRole('heading',{name:'Configure Your Session'}).waitFor()
  if(process.env.SCREENSHOT_DIR)await page.screenshot({path:process.env.SCREENSHOT_DIR+'/orator-generate.png',fullPage:true})
  await page.reload();await page.getByRole('heading',{name:'Configure Your Session'}).waitFor();assert.ok(requests.some(r=>r.path==='/api/auth/me'&&r.auth==='Bearer synthetic-test-token'),'reload validates fixture token')
  rejectHistory=true;await page.getByRole('link',{name:'History',exact:true}).click();await page.getByRole('heading',{name:'Welcome back'}).waitFor();assert.equal(new URL(page.url()).pathname,'/login');assert.equal(await page.evaluate(()=>localStorage.getItem('token')),null);assert.equal(await page.evaluate(()=>localStorage.getItem('user')),null)
  assert.deepEqual(errors,[])
  console.log('PASS public pages, protected redirect, Axios login JSON/auth header, authenticated navigation, reload auth, 401 token clearing. Synthetic browser API fixtures only; no backend/DB/provider calls.')
 }finally{if(browser)await browser.close();server.kill()}
})().catch(e=>{console.error(e);process.exitCode=1})
