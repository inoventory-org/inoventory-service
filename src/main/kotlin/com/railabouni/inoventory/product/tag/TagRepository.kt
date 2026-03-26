package com.railabouni.inoventory.product.tag

import com.railabouni.inoventory.product.tag.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, Long>