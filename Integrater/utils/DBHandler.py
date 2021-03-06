
from sqlalchemy import Column, BIGINT, BLOB, TEXT, VARCHAR, INT, FLOAT, BOOLEAN, ForeignKey, create_engine
from sqlalchemy.dialects.mysql import DATETIME
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, relationship
Base=declarative_base()

class Tag(Base):

	# table name
	__tablename__='tags'

	id=Column(BIGINT,primary_key=True,nullable=False)
	data=Column(BLOB,nullable=True)
	image_id=Column(BIGINT,nullable=False)
	project_id=Column(BIGINT,nullable=False)
	worker_id=Column(BIGINT,nullable=False)
	xml_file=Column(TEXT,nullable=True)
	is_result=Column(BOOLEAN,nullable=False)


class User(Base):
	__tablename__='users'
	id=Column(BIGINT,primary_key=True,nullable=False)
	dtype=Column(VARCHAR(31),nullable=False)
	email=Column(VARCHAR(255),nullable=True)
	intro=Column(VARCHAR(255),nullable=True)
	name=Column(VARCHAR(255),nullable=True)
	pw=Column(VARCHAR(255),nullable=True)
	money=Column(BIGINT,nullable=False)
	exp=Column(INT,nullable=True)
	level=Column(INT,nullable=True)
	passed_tag_num=Column(INT,nullable=True)
	tag_num=Column(INT,nullable=True)
	# one  -> many
	# user -> ability
	abilities=relationship("Ability",backref="user")

class Label(Base):
	__tablename__='labels'
	name=Column(VARCHAR(255),nullable=True,primary_key=True)

	# 	one  -> many
	# 	label -> ability
	abilities=relationship("Ability",backref="label")

class Ability(Base):
	__tablename__='abilities'
	id = Column(BIGINT, primary_key=True, nullable=False)
	accuracy=Column(FLOAT,nullable=False)
	efficiency = Column(FLOAT, nullable=False)
	bias=Column(INT,nullable=False)
	label_history_num=Column(INT,nullable=False)
	user_id=Column(BIGINT,ForeignKey('users.id'))
	label_name=Column(VARCHAR(255),ForeignKey('labels.name'))

class Image(Base):
	__tablename__='images'
	id=Column(BIGINT,primary_key=True,nullable=False)
	project_id=Column(BIGINT,nullable=False)
	height=Column(INT,nullable=False)
	width=Column(INT,nullable=False)
	current_num=Column(INT,nullable=False)      #这个图片当前被做的次数
	max_num=Column(INT,nullable=False)
	min_num=Column(INT,nullable=False)
	is_finished=Column(BOOLEAN,nullable=False)
	is_test=Column(BOOLEAN,nullable=False)
	picture=Column(BLOB)

class CommitEvent(Base):
	__tablename__='commits'
	id=Column(BIGINT,primary_key=True,nullable=False)
	accuracy=Column(FLOAT,nullable=False)
	efficiency=Column(FLOAT,nullable=False)
	commit_msg=Column(VARCHAR(25),nullable=True)
	commit_time=Column(DATETIME,nullable=True)
	imageid=Column(BIGINT,nullable=False)
	projectid=Column(BIGINT,nullable=False)
	tagid=Column(BIGINT,nullable=False)
	workerid=Column(BIGINT,nullable=False)

class TestProject(Base):
	__tablename__='test_projects'
	id=Column(BIGINT,primary_key=True,nullable=False)
	invite_code=Column(VARCHAR(255),nullable=True)
	mark_node=Column(INT,nullable=True)
	pic_num=Column(INT,nullable=False)

class Project(Base):
	__tablename__='projects'
	id=Column(BIGINT,primary_key=True)
	project_name=Column(VARCHAR)
	test_project_id=Column(BIGINT,nullable=True)

class Project_Label(Base):
	__tablename__='project_labels'
	project_id=Column(BIGINT,primary_key=True)
	labels=Column(VARCHAR,primary_key=True)




# initialize connection
def setup_db():
	engine=create_engine('mysql+mysqlconnector://root:31415926@localhost:3306/letsdo')#,echo=True
	DBSession=sessionmaker(bind=engine)
	return DBSession()

'''
检查Image是否需要进行整合答案，返回boolean
'''
def image_need_integrate(image_id):
	session=setup_db()
	image=session.query(Image).filter(Image.id==image_id).one()
	session.close()
	return image.current_num>=image.min_num

'''
得到image对象对应的tag，返回类型是list
'''
def get_image_tags(image_id):
	session=setup_db()
	tags=session.query(Tag).filter(Tag.image_id==image_id).all()
	session.close()
	return tags

def get_image_commit(image_id):
	session=setup_db()
	commits=session.query(CommitEvent).filter(CommitEvent.imageid==image_id).all()
	session.close()
	return commits

def get_test_project_images(project_id):
	session=setup_db()
	test_project_images=session.query(Image).filter(Image.project_id==project_id,Image.is_test==True).all()
	session.close()
	return test_project_images


'''
add:        session.add(obj)
delete:     session.query(class)[.filter()].delete()
modify:     session.query(class).filter(conditions).update({class.attribute:new_val}[, other options ])
search:     session.query()[.filter()].(all()/one())
commit:     session.commit()
close:      session.close()
'''
if __name__=='__main__':
	session=setup_db()
	# user=User(id=300,dtype='WK',email='email',intro='intro',name='name',pw='pw',money=0,exp=0,level=0,passed_tag_num=0,tag_num=0)
	# label=Label(name='label')
	# ability=Ability(id=299,accuracy=0.92,bias=121,user_id=100,label_id=199)
	# image=Image(id=1,project_id=1,height=100,width=100,current_num=2,min_num=2,max_num=5,is_finished=False,
	#             is_test=True)
	# tag1=Tag(id=1,image_id=1,project_id=1,worker_id=2,is_result=False)
	# tag2=Tag(id=2,image_id=1,project_id=1,worker_id=2,is_result=False)
	#
	# session.add(image)
	# session.add(tag1)
	# session.add(tag2)
	# session.commit()

	# session.add(user)
	# session.commit()

	# res=image_need_integrate(1)
	# print(res)
	#
	# res=get_image_tags(1)
	# print(type(res))
	# print(res)

	# project=session.query(Project).filter(Project.project_name=='test1').one()
	# print(project.id)
	# pls=session.query(Project_Label).filter(Project_Label.project_id==3).all()
	# for pl in pls:
	# 	print(pl.labels)
	# 	print(type(pl.labels))
	# 	print(type(pl))



	session.close()