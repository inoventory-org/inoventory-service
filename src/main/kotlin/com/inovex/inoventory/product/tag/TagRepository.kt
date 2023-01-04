package com.inovex.inoventory.product.tag

import com.inovex.inoventory.product.tag.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, Long>