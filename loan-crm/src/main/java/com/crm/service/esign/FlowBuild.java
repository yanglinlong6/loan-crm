package com.crm.service.esign;

import com.crm.service.esign.flow.Signer;
import com.crm.service.esign.util.DefineException;
import com.crm.service.esign.vo.*;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class FlowBuild {

    private FlowInfo flowInfo; // 流程信息

    private List<Attachment> attachments; // 附件列表

    private List<Copier> copiers; // 抄送人列表

    private List<Doc> docs; // 签署文件列表

    private List<Signer> signers; // 签署人列表

    private FlowBuild(){}

    public static FlowBuild createFlow(){
        return new FlowBuild();
    }

    /**
     * 设置签约流程信息
     * @param flowInfo FlowInfo
     * @return FlowBuild 当前[FlowBuild]对象
     */
    public FlowBuild setFlowInfo(FlowInfo flowInfo){
        this.flowInfo = flowInfo;
        return this;
    }

    /**
     * 添加合同附件
     * @param fileId 文件id
     * @param attachmentName 附件名称
     * @return FlowBuild当前对象
     * @throws DefineException
     */
    public FlowBuild addAttachment(String fileId,String attachmentName) throws DefineException {
        if(StringUtils.isBlank(fileId) || StringUtils.isBlank(attachmentName)){
            return this;
        }
        Attachment attachment = new Attachment(fileId,attachmentName);
        if(CollectionUtils.isEmpty(attachments)){
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
        return this;
    }

    /**
     * 添加抄送人
     *   注: 抄送人指不参与签署但需要查看签署文件的人,签署流程归档后抄送人收到通知
     * @param copierAccountId <必填>抄送人account id
     * @param copierIdentityAccountType <必填> 抄送主体类型, 0-个人, 1-企业
     * @param copierIdentityAccountId 抄送主体账号id
     * @return FlowBuild 当前对象
     */
    public FlowBuild addCopier(String copierAccountId,Integer copierIdentityAccountType,String copierIdentityAccountId){
        if(StringUtils.isBlank(copierAccountId) && JudgeUtil.in(copierIdentityAccountType,1,2)){
            return this;
        }
        Copier copier = new Copier(copierAccountId,copierIdentityAccountType,copierIdentityAccountId);
        if(CollectionUtils.isEmpty(copiers)){
            copiers = new ArrayList();
        }
        copiers.add(copier);
        return this;
    }

    /**
     * 增加签署的合同文件
     * @param fileId 文件id
     * @param fileName 文件名称(必须和上传的文件文件名称一致)
     * @return
     */
    public FlowBuild addDoc(String fileId,String fileName){
        if(StringUtils.isBlank(fileId) || StringUtils.isBlank(fileName)){
            return this;
        }
        Doc doc = new Doc(fileId,fileName);
        if(CollectionUtils.isEmpty(docs)){
            docs = new ArrayList<>();
        }
        docs.add(doc);
        return this;
    }

    public FlowBuild addSigner(Signer signer){
        if(null == signer)
            return this;
        if(CollectionUtils.isEmpty(signers)){
            signers = new ArrayList<>();
        }
        signers.add(signer);
        return this;
    }


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
