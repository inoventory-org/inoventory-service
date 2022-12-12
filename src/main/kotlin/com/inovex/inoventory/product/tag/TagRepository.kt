package com.inovex.inoventory.product.tag

import com.inovex.inoventory.product.tag.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long>