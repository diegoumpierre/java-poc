package com.poc.tenant.service;

import com.poc.tenant.BaseUnitTest;
import com.poc.tenant.domain.Supplier;
import com.poc.tenant.exception.ResourceNotFoundException;
import com.poc.tenant.model.request.SupplierRequest;
import com.poc.tenant.model.response.SupplierResponse;
import com.poc.tenant.repository.SupplierRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SupplierService Unit Tests")
class SupplierServiceUnitTest extends BaseUnitTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService service;

    private Supplier buildSupplier(UUID id, String name, String category) {
        return Supplier.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .name(name)
                .email(name.toLowerCase().replace(" ", "") + "@example.com")
                .phone("51999990000")
                .document("12345678000100")
                .category(category)
                .address("Rua Teste 123")
                .city("Porto Alegre")
                .state("RS")
                .notes("Fornecedor teste")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Should return active suppliers for current tenant")
        void shouldReturnActiveSuppliers() {
            List<Supplier> suppliers = List.of(
                    buildSupplier(randomId(), "Materiais ABC", "MATERIAIS"),
                    buildSupplier(randomId(), "Eletrica XYZ", "ELETRICA")
            );
            when(supplierRepository.findByTenantIdAndActiveTrue(TEST_TENANT_ID)).thenReturn(suppliers);

            List<SupplierResponse> result = service.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Materiais ABC");
            assertThat(result.get(1).getName()).isEqualTo("Eletrica XYZ");
            verify(supplierRepository).findByTenantIdAndActiveTrue(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("Should return empty list when no suppliers")
        void shouldReturnEmptyList() {
            when(supplierRepository.findByTenantIdAndActiveTrue(TEST_TENANT_ID)).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Should return supplier when found")
        void shouldReturnSupplier() {
            UUID id = randomId();
            Supplier supplier = buildSupplier(id, "Materiais ABC", "MATERIAIS");
            when(supplierRepository.findByIdAndTenantId(id, TEST_TENANT_ID)).thenReturn(Optional.of(supplier));

            SupplierResponse result = service.findById(id);

            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getName()).isEqualTo("Materiais ABC");
            assertThat(result.getCategory()).isEqualTo("MATERIAIS");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            UUID id = randomId();
            when(supplierRepository.findByIdAndTenantId(id, TEST_TENANT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Supplier not found");
        }
    }

    @Nested
    @DisplayName("findByCategory")
    class FindByCategory {

        @Test
        @DisplayName("Should return suppliers filtered by category")
        void shouldFilterByCategory() {
            List<Supplier> suppliers = List.of(
                    buildSupplier(randomId(), "Materiais ABC", "MATERIAIS")
            );
            when(supplierRepository.findByCategoryAndTenantId("MATERIAIS", TEST_TENANT_ID)).thenReturn(suppliers);

            List<SupplierResponse> result = service.findByCategory("MATERIAIS");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategory()).isEqualTo("MATERIAIS");
        }
    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("Should search suppliers by query")
        void shouldSearch() {
            List<Supplier> suppliers = List.of(
                    buildSupplier(randomId(), "Materiais ABC", "MATERIAIS")
            );
            when(supplierRepository.searchByTenantId(TEST_TENANT_ID, "ABC")).thenReturn(suppliers);

            List<SupplierResponse> result = service.search("ABC");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).contains("ABC");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create supplier with tenant context")
        void shouldCreateSupplier() {
            SupplierRequest request = SupplierRequest.builder()
                    .name("Novo Fornecedor")
                    .email("novo@example.com")
                    .phone("51999990001")
                    .document("98765432000100")
                    .category("HIDRAULICA")
                    .address("Rua Nova 456")
                    .city("Canoas")
                    .state("RS")
                    .notes("Novo fornecedor de hidraulica")
                    .build();

            when(supplierRepository.save(any(Supplier.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            SupplierResponse result = service.create(request);

            ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
            verify(supplierRepository).save(captor.capture());
            Supplier saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getTenantId()).isEqualTo(TEST_TENANT_ID);
            assertThat(saved.getName()).isEqualTo("Novo Fornecedor");
            assertThat(saved.getCategory()).isEqualTo("HIDRAULICA");
            assertThat(saved.getActive()).isTrue();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(result.getName()).isEqualTo("Novo Fornecedor");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update all supplier fields")
        void shouldUpdateSupplier() {
            UUID id = randomId();
            Supplier existing = buildSupplier(id, "Antigo Nome", "MATERIAIS");
            when(supplierRepository.findByIdAndTenantId(id, TEST_TENANT_ID)).thenReturn(Optional.of(existing));
            when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> inv.getArgument(0));

            SupplierRequest request = SupplierRequest.builder()
                    .name("Novo Nome")
                    .email("novo@example.com")
                    .phone("51999990002")
                    .document("11111111000111")
                    .category("ELETRICA")
                    .address("Rua Atualizada 789")
                    .city("Gravatai")
                    .state("RS")
                    .notes("Atualizado")
                    .build();

            SupplierResponse result = service.update(id, request);

            ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
            verify(supplierRepository).save(captor.capture());
            Supplier saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("Novo Nome");
            assertThat(saved.getCategory()).isEqualTo("ELETRICA");
            assertThat(saved.getCity()).isEqualTo("Gravatai");
            assertThat(saved.isNew()).isFalse();
            assertThat(result.getName()).isEqualTo("Novo Nome");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            UUID id = randomId();
            when(supplierRepository.findByIdAndTenantId(id, TEST_TENANT_ID)).thenReturn(Optional.empty());

            SupplierRequest request = SupplierRequest.builder().name("X").build();

            assertThatThrownBy(() -> service.update(id, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(supplierRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete (soft-delete)")
    class Delete {

        @Test
        @DisplayName("Should soft-delete by setting active=false")
        void shouldSoftDelete() {
            UUID id = randomId();
            Supplier existing = buildSupplier(id, "To Delete", "MATERIAIS");
            when(supplierRepository.findByIdAndTenantId(id, TEST_TENANT_ID)).thenReturn(Optional.of(existing));
            when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> inv.getArgument(0));

            service.delete(id);

            ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
            verify(supplierRepository).save(captor.capture());
            Supplier saved = captor.getValue();

            assertThat(saved.getActive()).isFalse();
            assertThat(saved.isNew()).isFalse();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            UUID id = randomId();
            when(supplierRepository.findByIdAndTenantId(id, TEST_TENANT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(supplierRepository, never()).save(any());
        }
    }
}
