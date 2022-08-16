package com.wangyit.test.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2022-07-30
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("qrcode")
@ApiModel(value="二维码对象", description="")
public class QRCode extends Model<QRCode> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sid", type = IdType.AUTO)
    @ApiModelProperty(value = "序号")
    private Integer sid;

    @ApiModelProperty(value = "二维码")
    private String qrcode;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "生成时间")
    private LocalDateTime generateTime;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expiryTime;

    @ApiModelProperty(value = "状态")
    private String status;


}
