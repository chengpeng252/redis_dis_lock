<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css"/>
    <title></title>
    <style type="text/css">
        body {
            font-family: pingfang sc light;
        }
        .center{
            text-align: center;
            width: 100%;
        }
    </style>
</head>
<body>
<!--��һҳ��ʼ-->
<div class="page" >
    <div class="center"><p>${templateName}</p></div>
    <div><p>iText����:${ITEXTUrl}</p></div>
    <div><p>FreeMarker����:${freeMarkerUrl}</p></div>
    <div><p>JFreeChart�̳�:${JFreeChartUrl}</p></div>
    <!--�ⲿ����-->
    <p>��̬logoͼ</p>
    <div>
        <img src="${imageUrl}" alt="���ŵ���" width="512" height="359"/>
    </div>
    <!--��̬���ɵ�ͼƬ-->
    <p>���±仯�Ա�ͼ</p>
    <div>
        <img src="${picUrl}" alt="�ҵ�ͼƬ" width="500" height="270"/>
    </div>
</div>
<!--��һҳ����-->
<!---��ҳ���-->
<span style="page-break-after:always;"></span>
<!--�ڶ�ҳ��ʼ-->
<div class="page">
    <div>�ڶ�ҳ��ʼ��</div>
    <div>�б�ֵ:</div>
    <div>
    <#list scores as item>
        <div><p>${item}</p></div>
    </#list>
    </div>
 
</div>
<!--�ڶ�ҳ����-->
</body>
</html>
