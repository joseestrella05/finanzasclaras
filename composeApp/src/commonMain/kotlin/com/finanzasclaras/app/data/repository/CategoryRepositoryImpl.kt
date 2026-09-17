package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.data.local.SeedData
import com.finanzasclaras.app.data.local.dao.CategoryDao
import com.finanzasclaras.app.data.local.entity.CategoryEntity
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> =
        categoryDao.getAll().map { entities ->
            if (entities.isEmpty()) {
                val defaults = SeedData.getDefaultCategories()
                categoryDao.insertAll(defaults)
                defaults.map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }

    override fun getByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getByType(TransactionType.toString(type)).map { entities ->
            if (entities.isEmpty()) {
                val defaults = SeedData.getDefaultCategories()
                categoryDao.insertAll(defaults)
                defaults.filter { it.type == TransactionType.toString(type) }.map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }

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
        val existing = categoryDao.getAll().first()
        val existingIds = existing.map { it.id }.toSet()
        val missing = SeedData.getDefaultCategories().filter { it.id !in existingIds }
        if (missing.isNotEmpty()) {
            categoryDao.insertAll(missing)
        }
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
