package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.data.local.dao.CategoryDao
import com.finanzasclaras.app.data.local.entity.CategoryEntity
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.core.common.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> =
        categoryDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getByType(TransactionType.toString(type)).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): Category? =
        categoryDao.getById(id)?.toDomain()

    override suspend fun create(category: Category) {
        categoryDao.insert(category.toEntity())
    }

    override suspend fun update(category: Category) {
        categoryDao.update(category.id, category.name, category.icon, category.color)
    }

    override suspend fun delete(id: String) {
        categoryDao.delete(id)
    }

    override suspend fun seedDefaultCategories() {
        val existing = categoryDao.getAll()
        // Only seed if empty
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id, name = name, nameEn = nameEn, icon = icon,
        type = TransactionType.fromString(type),
        color = color, isDefault = isDefault
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id, name = name, nameEn = nameEn, icon = icon,
        type = TransactionType.toString(type),
        color = color, isDefault = isDefault, orderIndex = 99
    )
}
