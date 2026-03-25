-- Supabase RLS policies for inoventory-service tables
-- Table names follow Hibernate defaults (entity class names in snake_case).

-- Inventory lists
alter table public.inventory_list_entity enable row level security;
create policy "lists_select_own"
    on public.inventory_list_entity
    for select
    using (user_id = auth.uid());
create policy "lists_insert_own"
    on public.inventory_list_entity
    for insert
    with check (user_id = auth.uid());
create policy "lists_update_own"
    on public.inventory_list_entity
    for update
    using (user_id = auth.uid())
    with check (user_id = auth.uid());
create policy "lists_delete_own"
    on public.inventory_list_entity
    for delete
    using (user_id = auth.uid());

-- List items (enforce via owning list)
alter table public.list_item_entity enable row level security;
create policy "list_items_select_own"
    on public.list_item_entity
    for select
    using (
        exists (
            select 1
            from public.inventory_list_entity l
            where l.id = list_item_entity.list_id
              and l.user_id = auth.uid()
        )
    );
create policy "list_items_insert_own"
    on public.list_item_entity
    for insert
    with check (
        exists (
            select 1
            from public.inventory_list_entity l
            where l.id = list_item_entity.list_id
              and l.user_id = auth.uid()
        )
    );
create policy "list_items_update_own"
    on public.list_item_entity
    for update
    using (
        exists (
            select 1
            from public.inventory_list_entity l
            where l.id = list_item_entity.list_id
              and l.user_id = auth.uid()
        )
    )
    with check (
        exists (
            select 1
            from public.inventory_list_entity l
            where l.id = list_item_entity.list_id
              and l.user_id = auth.uid()
        )
    );
create policy "list_items_delete_own"
    on public.list_item_entity
    for delete
    using (
        exists (
            select 1
            from public.inventory_list_entity l
            where l.id = list_item_entity.list_id
              and l.user_id = auth.uid()
        )
    );

-- Permissions
alter table public.permission_entity enable row level security;
create policy "permissions_select_own"
    on public.permission_entity
    for select
    using (user_id = auth.uid());
create policy "permissions_insert_own"
    on public.permission_entity
    for insert
    with check (user_id = auth.uid());
create policy "permissions_update_own"
    on public.permission_entity
    for update
    using (user_id = auth.uid())
    with check (user_id = auth.uid());
create policy "permissions_delete_own"
    on public.permission_entity
    for delete
    using (user_id = auth.uid());
