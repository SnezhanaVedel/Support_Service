PGDMP     3                    |            postgres    15.4    15.4 .    )           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            *           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            +           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            ,           1262    5    postgres    DATABASE     |   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';
    DROP DATABASE postgres;
                postgres    false            -           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    3372                        3079    16929 	   adminpack 	   EXTENSION     A   CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;
    DROP EXTENSION adminpack;
                   false            .           0    0    EXTENSION adminpack    COMMENT     M   COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';
                        false    2            �            1255    67311    notify_report_created()    FUNCTION     �   CREATE FUNCTION public.notify_report_created() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    PERFORM pg_notify('report_created', NEW.request_id::text);
    RETURN NEW;
END;
$$;
 .   DROP FUNCTION public.notify_report_created();
       public          postgres    false            �            1255    67313    notify_report_updated()    FUNCTION     �   CREATE FUNCTION public.notify_report_updated() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    PERFORM pg_notify('report_updated', NEW.request_id::text);
    RETURN NEW;
END;
$$;
 .   DROP FUNCTION public.notify_report_updated();
       public          postgres    false            �            1255    67248    notify_request_created()    FUNCTION     �   CREATE FUNCTION public.notify_request_created() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    PERFORM pg_notify('request_created', NEW.id::text);
    RETURN NEW;
END;
$$;
 /   DROP FUNCTION public.notify_request_created();
       public          postgres    false            �            1255    67249    notify_request_updated()    FUNCTION     �   CREATE FUNCTION public.notify_request_updated() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    PERFORM pg_notify('request_updated', NEW.id::text);
    RETURN NEW;
END;
$$;
 /   DROP FUNCTION public.notify_request_updated();
       public          postgres    false            �            1259    67250 	   equipment    TABLE     �   CREATE TABLE public.equipment (
    serial_num character varying(20) NOT NULL,
    equip_name character varying(100),
    equip_type character varying(20),
    condition character varying(20),
    detals text,
    location character varying(20)
);
    DROP TABLE public.equipment;
       public         heap    postgres    false            �            1259    67255    members    TABLE     �   CREATE TABLE public.members (
    id integer NOT NULL,
    name character varying(100),
    phone character varying(20),
    email character varying(50),
    login character varying(50),
    pass character varying(50),
    role character varying(20)
);
    DROP TABLE public.members;
       public         heap    postgres    false            �            1259    67258    members_id_seq    SEQUENCE     �   CREATE SEQUENCE public.members_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.members_id_seq;
       public          postgres    false    216            /           0    0    members_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.members_id_seq OWNED BY public.members.id;
          public          postgres    false    217            �            1259    67259    orders    TABLE     �   CREATE TABLE public.orders (
    id integer NOT NULL,
    request_id integer,
    resource_type character varying(20),
    resource_name character varying(20),
    cost numeric
);
    DROP TABLE public.orders;
       public         heap    postgres    false            �            1259    67264    orders_id_seq    SEQUENCE     �   CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.orders_id_seq;
       public          postgres    false    218            0           0    0    orders_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;
          public          postgres    false    219            �            1259    67265    reports    TABLE     �   CREATE TABLE public.reports (
    request_id integer NOT NULL,
    repair_type character varying(50),
    "time" integer,
    cost numeric,
    resources text,
    reason text,
    help text
);
    DROP TABLE public.reports;
       public         heap    postgres    false            �            1259    67270    requests    TABLE     �   CREATE TABLE public.requests (
    id integer NOT NULL,
    serial_num character varying(20),
    problem_desc text,
    request_comments text,
    status character varying(20),
    date_start date,
    member_id integer
);
    DROP TABLE public.requests;
       public         heap    postgres    false            �            1259    67275    requests_id_seq    SEQUENCE     �   CREATE SEQUENCE public.requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.requests_id_seq;
       public          postgres    false    221            1           0    0    requests_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.requests_id_seq OWNED BY public.requests.id;
          public          postgres    false    222            |           2604    67276 
   members id    DEFAULT     h   ALTER TABLE ONLY public.members ALTER COLUMN id SET DEFAULT nextval('public.members_id_seq'::regclass);
 9   ALTER TABLE public.members ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    217    216            }           2604    67277 	   orders id    DEFAULT     f   ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);
 8   ALTER TABLE public.orders ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    219    218            ~           2604    67278    requests id    DEFAULT     j   ALTER TABLE ONLY public.requests ALTER COLUMN id SET DEFAULT nextval('public.requests_id_seq'::regclass);
 :   ALTER TABLE public.requests ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    222    221                      0    67250 	   equipment 
   TABLE DATA           d   COPY public.equipment (serial_num, equip_name, equip_type, condition, detals, location) FROM stdin;
    public          postgres    false    215   �5                  0    67255    members 
   TABLE DATA           L   COPY public.members (id, name, phone, email, login, pass, role) FROM stdin;
    public          postgres    false    216   [9       "          0    67259    orders 
   TABLE DATA           T   COPY public.orders (id, request_id, resource_type, resource_name, cost) FROM stdin;
    public          postgres    false    218   i:       $          0    67265    reports 
   TABLE DATA           a   COPY public.reports (request_id, repair_type, "time", cost, resources, reason, help) FROM stdin;
    public          postgres    false    220   �:       %          0    67270    requests 
   TABLE DATA           q   COPY public.requests (id, serial_num, problem_desc, request_comments, status, date_start, member_id) FROM stdin;
    public          postgres    false    221   �;       2           0    0    members_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.members_id_seq', 6, true);
          public          postgres    false    217            3           0    0    orders_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.orders_id_seq', 7, true);
          public          postgres    false    219            4           0    0    requests_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.requests_id_seq', 20, true);
          public          postgres    false    222            �           2606    67280    equipment equipment_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.equipment
    ADD CONSTRAINT equipment_pkey PRIMARY KEY (serial_num);
 B   ALTER TABLE ONLY public.equipment DROP CONSTRAINT equipment_pkey;
       public            postgres    false    215            �           2606    67282    members members_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.members DROP CONSTRAINT members_pkey;
       public            postgres    false    216            �           2606    67284    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            postgres    false    218            �           2606    67286    reports reports_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (request_id);
 >   ALTER TABLE ONLY public.reports DROP CONSTRAINT reports_pkey;
       public            postgres    false    220            �           2606    67288    requests requests_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.requests DROP CONSTRAINT requests_pkey;
       public            postgres    false    221            �           2620    67312    reports trigger_report_created    TRIGGER     �   CREATE TRIGGER trigger_report_created AFTER INSERT ON public.reports FOR EACH ROW EXECUTE FUNCTION public.notify_report_created();
 7   DROP TRIGGER trigger_report_created ON public.reports;
       public          postgres    false    236    220            �           2620    67314    reports trigger_report_updated    TRIGGER     �   CREATE TRIGGER trigger_report_updated AFTER UPDATE ON public.reports FOR EACH ROW EXECUTE FUNCTION public.notify_report_updated();
 7   DROP TRIGGER trigger_report_updated ON public.reports;
       public          postgres    false    220    237            �           2620    67289     requests trigger_request_created    TRIGGER     �   CREATE TRIGGER trigger_request_created AFTER INSERT ON public.requests FOR EACH ROW EXECUTE FUNCTION public.notify_request_created();
 9   DROP TRIGGER trigger_request_created ON public.requests;
       public          postgres    false    223    221            �           2620    67290     requests trigger_request_updated    TRIGGER     �   CREATE TRIGGER trigger_request_updated AFTER UPDATE ON public.requests FOR EACH ROW EXECUTE FUNCTION public.notify_request_updated();
 9   DROP TRIGGER trigger_request_updated ON public.requests;
       public          postgres    false    235    221            �           2606    67291    orders orders_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_request_id_fkey;
       public          postgres    false    3208    221    218            �           2606    67296    reports reports_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 I   ALTER TABLE ONLY public.reports DROP CONSTRAINT reports_request_id_fkey;
       public          postgres    false    221    220    3208            �           2606    67301     requests requests_member_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_member_id_fkey FOREIGN KEY (member_id) REFERENCES public.members(id);
 J   ALTER TABLE ONLY public.requests DROP CONSTRAINT requests_member_id_fkey;
       public          postgres    false    3202    221    216            �           2606    67306 !   requests requests_serial_num_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_serial_num_fkey FOREIGN KEY (serial_num) REFERENCES public.equipment(serial_num);
 K   ALTER TABLE ONLY public.requests DROP CONSTRAINT requests_serial_num_fkey;
       public          postgres    false    215    3200    221               �  x���Io�P�����*�	=�M�%Ū�Vq���Zv�l�R�@�TZ!8pN�Q��+���l'�n�r��{�ߛ������A��+����9^�o�� �p�����ms��C`�x�n� ���e�ot�TKp}qV����9X��8ܥS3 ߸�"e����������
�����\}�VI<t��	�g�(���Ū��$l~bw��3�-�/p���
�t����m�
/-Nl�,���rP��Aݰ�GvQƴ ��yy}U�觮�"���j.��l*�l	d)��(V��UMd�K�Y=��B��t`�r�f϶b�E�Ij9��s�8��֜mo&0YEQm�`��3ÂJnM+>T�C�Bsô6�փb��O'-y*��R%�~�wwLY��P%.iPo9F�яm�!�����q:/�GKPU"�Ɽ8k�e�h�w�++���~l��IY���,��]��m�ٲ6��,�)���hJ�6׳�F����TV|�뭇Ζ�w�=�q�Y>U~��-�v�p@�y�BD'��� �hM�Z�d��<�W�EI"%�!�<ĸ+g��|��bE"e���<Zm:� �(y���n�z��l$~�T�s�
��}�$% �s���.8�s���y�i۩[a�l_�E�]�I����A����t�8C��WDZ*d�>SIgN��
�J�t^+���s�,�~�s�SLc]�$�xz1��c0y��1a��t�T��LAoK�e!���*Wz���^p�|�u���d:��r	��4�������ۦ�X�-���@g~0
\�]�/�����G�Xq��Ŀ��߈5dJ�}��C�^Q��G�E�q@�>%�<ġ�rĄ��v;4Zm��@����*�Ga6�MJƠo��������O��U)�)�,����)R5��"E<9�P��PPB˚���H����<�[ɠ�          �   x�U��J�@��7O1K��0ӤIv
nwn݌Zl0�`�#q%�)nt!��JlkA�3�y#oĸ��{.��Ix�7x����Ox��^ۙ��|�cag�j��mGb+	��PA�+��f�i�����J���9�u�uOLN�4O*�;�)�c�
kNg&�/�|��Ѭ�L�Y6W��Qe����2��	�ezϔWô0S��Q<J���#�>q1&9��]D������?��L%���tq9(Ǖ�u=�����o      "   o   x�3�44�0�bÅ}�.l��x��j���+I�Q�4430�2�44⼰���[/���ih�d`ij��k`hd�i	Tb�0�b3PP� Gc�`go�N�1z\\\ ��)�      $   �   x�e�K�@D�=�����x�O4��411n]��+t���q���|RS�j�Z�w�`}�朖d�0�*)w�g'kY���%�����|���B�"�r���܏Ӝ��b��v=�9~��`.�j�r��]ɱ��K��^(����+��N���:9�(�&S�CV[�iKDq�I�R�� o�=H=������ތ      %   f  x���MN�0���)|�F����B������8L�.��P;$Ă����%�fnĳ�Pv���;�o�7�6���B)#�
SK5���������Fe� 6��Dz�*{e��O�K847�-��V�QA��->IK��<�hEu(d-қk������Oh�/��璧�o	��*8�2��c�P4߫`"�N�\�7���T�9s�]�O$��_��Y�#�
��F���(�[Ssb+��w�e8�Ղ^-!xQ�cq�1pY<�$4lV� s��Gb�c�Iw�v�]�zZ�l��w�x�yЋ¿�~{s0�����������倖@Bw�ͤ�����@���� ~�_��     