package com.hm.stock.domain.ws.entity;

import com.hm.stock.domain.member.entity.Member;
import lombok.Data;

@Data
public class WsMember {

    private Member member;

    private Long unreadMsg;

}
