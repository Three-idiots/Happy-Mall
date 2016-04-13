<?php
///////////////////////////////////////////////
/**********************************************
WeMall客户端Api,刘德位编写
Bug反馈QQ:793554262
**********************************************/
///////////////////////////////////////////////
header("Content-type:text/html;charset=utf-8");
global $_SERVER;
$agent = $_SERVER['HTTP_USER_AGENT'];
if($agent!="WeMall_Client"){
	echo "非法请求";
	exit();
}
?>
<?php
//sql注入过滤-by360
function customError($errno, $errstr, $errfile, $errline)
{ 
 echo "Error number:[$errno],error on line $errline in $errfile";
 die();
}
set_error_handler("customError",E_ERROR);
$getfilter="'|(and|or)\\b.+?(>|<|=|in|like)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
$postfilter="\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
$cookiefilter="\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
function StopAttack($StrFiltKey,$StrFiltValue,$ArrFiltReq){  

if(is_array($StrFiltValue))
{
    $StrFiltValue=implode($StrFiltValue);
}  
if (preg_match("/".$ArrFiltReq."/is",$StrFiltValue)==1){   
        print "非法请求";
        exit();
}      
}  

foreach($_GET as $key=>$value){ 
	StopAttack($key,$value,$getfilter);
}
foreach($_POST as $key=>$value){ 
	StopAttack($key,$value,$postfilter);
}
foreach($_COOKIE as $key=>$value){ 
	StopAttack($key,$value,$cookiefilter);
}

?>
<?php include('../Public/Conf/config.php');?>
<?php
	$tag=$_GET['tag'];
if(!isset($_GET['tag'])){
	echo "非法请求";
	}
else{
	switch ($tag) {
		case 'wemall_query_myorder':
			wemall_query_myorder();
			break;
		case 'wemall_update_myadder':
			wemall_update_myadder();
			break;
		case 'wemall_query_goods':
			wemall_query_goods();
			break;
		case 'wemall_add_order':
			wemall_add_order();
			break;
		case 'wemall_update_passwd':
			wemall_update_passwd();
			break;
		case 'wemall_update_head':
			wemall_update_head();
			break;
		case 'wemall_login_check':
			wemall_login_check();
			break;
		case 'wemall_rec_passwd':
			wemall_rec_passwd();
			break;
		case 'wemall_user_regist':
			wemall_user_regist();
			break;
		case 'wemall_query_menu':
			wemall_query_menu();
			break;
		default:
			echo "非法请求";
			break;
	}
}
?>
<?php
//订单查询@@@
function wemall_query_myorder(){
	$uid=$_POST['uid'];//uid
	echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	echo "<root>\n";
	if (isset($_POST['uid'])){
		$result = @mysql_query("select * from ".DB_PREFIX."order where user_id=(select id from ".DB_PREFIX."user where uid='$uid') order by time desc") or die("错误，请重试");
		while ($row=mysql_fetch_array($result)) {
				echo "<orders id=\"".$row[id]."\">"."<orderid>".$row[orderid]."</orderid>"."<totalprice>".$row[totalprice]."</totalprice>"."<pay_style>".$row[pay_style]."</pay_style>"."<pay_status>".$row[pay_status]."</pay_status>"."<note>".$row[note]."</note>"."<order_status>".$row[order_states]."</order_status>"."<time>".$row[time]."</time>"."<cartdata>".$row[cartdata]."</cartdata></orders>\n";
			}
		}
	echo "</root>";
}
//更新用户收货地址
function wemall_update_myadder(){
	$uid=$_POST['uid'];
	$address=base64_decode($_POST['address']);
	if (isset($_POST['uid'])&&isset($_POST['address'])){
		$result =@mysql_query("select uid from ".DB_PREFIX."user where uid='$uid' limit 1") or die("");
		if (mysql_num_rows($result)==0){
				echo "0";
		}
		elseif (mysql_num_rows($result)==1){
				@mysql_query("update ".DB_PREFIX."user set address='$address' where uid='$uid'") or die("");
				echo "1";
		}
	}
	else{
		echo "非法请求";
	}
}
//查询商品
function wemall_query_goods(){
	$preg = "/<\/?[^>]+>/i";//正则表达式,剔除详情里的html标签
	$type=$_POST['type'];//分类标志
	if (isset($_POST['type'])){
		$sql = "select * from ".DB_PREFIX."good where menu_id='$type' and status=1";
	}
	else{
		$sql = "select * from ".DB_PREFIX."good where status=1";
	}
	$result = mysql_query($sql);
	echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	echo "<items>\n";
	while ($row=mysql_fetch_array($result)) {
		echo "<item id=\"$row[id]\">"."<typeid>".$row[menu_id]."</typeid>"."<name>".$row[name]."</name>"."<image>".$row[image]."</image>"."<intro>".str_replace('	','',preg_replace($preg,'',$row[detail]))."</intro>"."<price>".$row[price]."</price>"."<priceno>".$row[old_price]."</priceno>"."</item>\n";
		}
	echo "</items>";
}
//增加订单
function wemall_add_order(){
	$uid=$_POST['uid'];
	$totalprice=$_POST['totalprice'];
	$paystyle=$_POST['paystyle'];
	$paystatus=$_POST['paystatus'];
	$note=$_POST['note'];
	$cartdata =$_POST['cartdata'];
	$orderid=date("YmdHis").get_millisecond();
	$userid=null;
	//获取userid
	$sql = "select id from ".DB_PREFIX."user where uid='$uid'";
	$result = mysql_query($sql);
	while ($row=mysql_fetch_array($result)) {
		$userid=$row['id'];
	}
	//校验并插入订单
	if(isset($_POST['uid'])){
	$sql = "insert into ".DB_PREFIX."order (id,user_id,orderid,totalprice,pay_style,pay_status,note,order_status,time,cartdata) values(NULL,'$userid','$orderid','$totalprice','$paystyle','$paystatus','$note',0,CURRENT_TIMESTAMP,'$cartdata')";
	mysql_query($sql);
	echo $sql;
	}
	else{
		echo "非法请求";
	}
}
//设置密码
function wemall_update_passwd(){
	$uid=$_POST['uid'];
	$oldusersubmit=md5($_POST['old']);
	$new=md5($_POST['new']);
	if (isset($_POST['uid'])&&isset($_POST['old'])&&isset($_POST['new'])){
	$result =@ mysql_query("select password from ".DB_PREFIX."user where uid='$uid' limit 1") or die("");
	while ($row=@mysql_fetch_array($result)) {

				if($row['password']!=$oldusersubmit){
					echo "0";
				}
				else if($row['password']==$oldusersubmit){
					$result=@mysql_query("update ".DB_PREFIX."user set password='$new' where uid='$uid' limit 1") or die("");
					echo "1";
				}
	}
	}
	else{
		echo "非法请求";
	}
}
//更新头像
function wemall_update_head(){
	if(isset($_POST['photo'])&&isset($_POST['uid'])){ 
	$file =fopen('./uploads/'.(md5($_POST['uid'])).'.jpg', "w");
	fwrite($file,base64_decode($_POST['photo']));
	fclose($file);
	}
	else{
	echo "非法请求";
	}
}
function wemall_login_check(){
	$account=$_POST['account'];//帐号
	$passwd=md5($_POST['passwd']);//密码
	if (isset($_POST['account'])){
	$result = @mysql_query("select * from ".DB_PREFIX."user where phone='$account' limit 1") or die("错误，请重试");
	if (mysql_num_rows($result)==0){
					echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					echo "<root>\n";
					echo "<result state=\"-1\"><uid></uid><name></name><phone></phone><address></address></result>\n";
					echo "</root>";
	}
	else{
			while ($row=mysql_fetch_array($result)) {
				if($row['password']==$passwd){
					echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					echo "<root>\n";
					echo "<result state=\"1\">"."<uid>".$row[uid]."</uid>"."<name>".$row[username]."</name>"."<phone>".$row[phone]."</phone>"."<address>".$row[address]."</address></result>\n";
					echo "</root>";
				}
				else{
					echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					echo "<root>\n";
					echo "<result state=\"0\"><uid></uid><name></name><phone></phone><address></address></result>\n";
					echo "</root>";
				}
			}
		}
	}
}
//恢复密码
function wemall_rec_passwd(){
	$phone=$_POST['phone'];
	$new=md5($_POST['new']);
	if (isset($_POST['phone'])&&isset($_POST['new'])){
	$result =@mysql_query("select phone from ".DB_PREFIX."user where phone='$phone' limit 1") or die("");
	if (mysql_num_rows($result)==0){
					echo "0";
	}
	elseif(mysql_num_rows($result)==1){
					$result=@mysql_query("update ".DB_PREFIX."user set password='$new' where phone='$phone' limit 1") or die("");
					echo "1";
	}
	}
	else{
		echo "非法请求";
	}
}
//注册用户
function wemall_user_regist(){
	$phone=$_POST['phone'];
	$name=base64_decode($_POST['name']);
	$saltuid=getRandStr($length=10);
	$uid=md5($_POST['phone'].$saltuid); //给用户构造一个唯一的UID,这里我们使用手机号加10位随机字符串的md5作为生成方案
	$passwd=md5($_POST['passwd']);
	if (isset($_POST['phone'])&&isset($_POST['name'])&&isset($_POST['passwd'])){
	$result =@ mysql_query("select uid from ".DB_PREFIX."user where phone='$phone'") or die("");
	if (mysql_num_rows($result)>0){
							echo "0";
	}
	elseif (mysql_num_rows($result)==0){
					$result=@mysql_query("insert into ".DB_PREFIX."user(uid,username,password,phone) values('$uid','$name','$passwd','$phone')") or die("");
					echo "1";
	}
	}
	else{
		echo "非法请求";
	}
}
//查询分组
function wemall_query_menu(){
	$sql = "select * from ".DB_PREFIX."menu order by id asc";
	$result = mysql_query($sql);
	echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	echo "<types>\n";
	while ($row=mysql_fetch_array($result)) {
		echo "<type id=\"$row[id]\">"."<name>".$row[name]."</name></type>\n";
					}
	echo "</types>";
}
/////////////////////////////////////////世界你好////////////////////////////////////////
function getRandStr($length) { 
	$str = 'abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
	$randString ='';
	$len = strlen($str)-1;
	for($i = 0;$i < $length;$i ++){
		$num = mt_rand(0, $len);
		$randString .= $str[$num];
		 }
		return $randString ;
}
function get_millisecond(){
	list($usec, $sec) = explode(" ", microtime());
	$msec=round($usec*1000);
	return $msec;        
	}
?>